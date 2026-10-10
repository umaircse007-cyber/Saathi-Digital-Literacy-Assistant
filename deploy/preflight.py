#!/usr/bin/env python3
"""Offline host-readiness check for the bounded Saathi pilot.

This does not start the gateway, contact a provider, open a socket, or print secret
values. It is intended to run on the selected host immediately before the separate
hosting/deployment phase.
"""
from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import stat
import sys

_NAME = re.compile(r"[A-Z][A-Z0-9_]*\Z")
_MODEL = re.compile(r"[A-Za-z0-9._/-]{1,100}\Z")
_REQUIRED = ("GEMINI_API_KEY", "GEMINI_MODEL", "GROQ_API_KEY", "GROQ_MODEL")


class PreflightError(ValueError):
    pass


def _private_regular_file(path: Path, label: str) -> None:
    try:
        mode = path.lstat().st_mode
    except OSError as error:
        raise PreflightError(f"{label} is unavailable") from error
    if stat.S_ISLNK(mode) or not stat.S_ISREG(mode):
        raise PreflightError(f"{label} must be a regular file")
    if mode & (stat.S_IRWXG | stat.S_IRWXO):
        raise PreflightError(f"{label} must not be readable, writable, or executable by group or others")


def _private_state_directory(path: Path) -> None:
    try:
        mode = path.lstat().st_mode
    except OSError as error:
        raise PreflightError("state directory is unavailable") from error
    if stat.S_ISLNK(mode) or not stat.S_ISDIR(mode):
        raise PreflightError("state directory must be a real directory")
    if mode & (stat.S_IRWXG | stat.S_IRWXO):
        raise PreflightError("state directory must not be accessible by group or others")


def read_environment(path: Path) -> dict[str, str]:
    """Parse a simple systemd EnvironmentFile-compatible KEY=value file.

    The result is intentionally limited to the deployment format: it rejects shell
    interpolation, control characters and duplicate keys rather than guessing.
    """
    _private_regular_file(path, "environment file")
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except (OSError, UnicodeError) as error:
        raise PreflightError("environment file cannot be read as UTF-8") from error
    values: dict[str, str] = {}
    for line in lines:
        item = line.strip()
        if not item or item.startswith("#"):
            continue
        name, separator, value = item.partition("=")
        if not separator or not _NAME.fullmatch(name) or name in values:
            raise PreflightError("environment file has an invalid or duplicate setting name")
        value = value.strip()
        if (len(value) >= 2 and value[0] == value[-1] and value[0] in "\"'"):
            value = value[1:-1]
        if not value or any(ord(char) < 33 or ord(char) > 126 for char in value):
            raise PreflightError(f"{name} is empty or contains unsupported characters")
        values[name] = value
    return values


def validate_environment(values: dict[str, str]) -> None:
    for name in _REQUIRED:
        value = values.get(name, "")
        if not value or value.startswith("REPLACE_"):
            raise PreflightError(f"{name} is not configured")
    for name in ("GEMINI_MODEL", "GROQ_MODEL"):
        value = values[name]
        if not _MODEL.fullmatch(value) or value.startswith("REPLACE_") or ".." in value:
            raise PreflightError(f"{name} is not a supported model identifier")
    primary = values.get("SAATHI_PRIMARY_PROVIDER", "groq")
    if primary not in {"gemini", "groq"}:
        raise PreflightError("SAATHI_PRIMARY_PROVIDER must be gemini or groq")
    try:
        total = int(values.get("SAATHI_MAX_PROVIDER_CALLS", "100"))
        each = int(values.get("SAATHI_MAX_CALLS_PER_PROVIDER", "50"))
    except ValueError as error:
        raise PreflightError("provider call caps must be whole numbers") from error
    if not 2 <= total <= 10_000 or not 1 <= each <= 5_000:
        raise PreflightError("provider call caps are outside the supported bounds")
    endpoint = values.get("SAATHI_SEARCH_ENDPOINT")
    if endpoint is not None and (not endpoint.startswith("https://") or any(char in endpoint for char in "?#@")):
        raise PreflightError("SAATHI_SEARCH_ENDPOINT must be a plain HTTPS origin/path")
    registry = values.get("SAATHI_RESEARCH_SOURCES")
    if registry is not None:
        candidate = Path(registry)
        if candidate.is_symlink() or not candidate.is_file() or not os.access(candidate, os.R_OK):
            raise PreflightError("SAATHI_RESEARCH_SOURCES must name a readable regular file")


def validate_template(repo_root: Path) -> None:
    for relative in ("deploy/gunicorn.conf.py", "deploy/Caddyfile", "deploy/saathi.service", "backend/hosted.py"):
        candidate = repo_root / relative
        if not candidate.is_file():
            raise PreflightError(f"deployment package is missing {relative}")
    caddy = (repo_root / "deploy/Caddyfile").read_text(encoding="utf-8")
    if "saathi.example.invalid" in caddy:
        raise PreflightError("replace the Caddy placeholder domain before deployment")


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description="Validate Saathi host configuration without starting it")
    parser.add_argument("--env-file", type=Path, required=True, help="private backend environment file")
    parser.add_argument("--state-dir", type=Path, required=True, help="private persistent state directory")
    parser.add_argument("--repo-root", type=Path, required=True, help="reviewed repository checkout")
    parser.add_argument("--skip-template-domain", action="store_true", help="allow the unedited Caddy template during packaging review only")
    args = parser.parse_args(argv)
    try:
        _private_state_directory(args.state_dir)
        values = read_environment(args.env_file)
        validate_environment(values)
        if args.skip_template_domain:
            for relative in ("deploy/gunicorn.conf.py", "deploy/Caddyfile", "deploy/saathi.service", "backend/hosted.py"):
                if not (args.repo_root / relative).is_file():
                    raise PreflightError(f"deployment package is missing {relative}")
        else:
            validate_template(args.repo_root)
    except PreflightError as error:
        print(f"Preflight failed: {error}", file=sys.stderr)
        return 2
    print("Preflight passed: private configuration and local deployment prerequisites are structurally valid. No server was started and no provider was contacted.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
