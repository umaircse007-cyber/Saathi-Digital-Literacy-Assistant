import contextlib
import importlib.util
import io
from pathlib import Path
import tempfile
import unittest


_spec = importlib.util.spec_from_file_location("saathi_preflight", Path(__file__).parents[2] / "deploy" / "preflight.py")
preflight = importlib.util.module_from_spec(_spec)
assert _spec and _spec.loader
_spec.loader.exec_module(preflight)


class DeploymentPreflightTests(unittest.TestCase):
    def make_environment(self, root: Path, extra: str = "") -> Path:
        path = root / "backend.env"
        path.write_text(
            "GEMINI_API_KEY=fake-gemini-key-for-test-only\n"
            "GEMINI_MODEL=gemini-test\n"
            "GROQ_API_KEY=fake-groq-key-for-test-only\n"
            "GROQ_MODEL=groq-test\n"
            + extra
        )
        path.chmod(0o600)
        return path

    def test_private_complete_environment_is_accepted_without_network_activity(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            env = self.make_environment(root)
            state = root / "state"; state.mkdir(); state.chmod(0o700)
            values = preflight.read_environment(env)
            preflight.validate_environment(values)
            preflight._private_state_directory(state)

    def test_placeholder_and_bad_permissions_are_rejected(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            env = self.make_environment(root)
            values = preflight.read_environment(env)
            values["GEMINI_MODEL"] = "REPLACE_WITH_SUPPORTED_MODEL"
            with self.assertRaises(preflight.PreflightError):
                preflight.validate_environment(values)
            env.chmod(0o644)
            with self.assertRaises(preflight.PreflightError):
                preflight.read_environment(env)

    def test_cli_never_echoes_secret_value_on_failure(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            secret = "fake-secret-must-not-appear"
            env = root / "backend.env"
            env.write_text(f"GEMINI_API_KEY={secret}\n")
            env.chmod(0o600)
            state = root / "state"; state.mkdir(); state.chmod(0o700)
            stderr = io.StringIO()
            with contextlib.redirect_stderr(stderr):
                code = preflight.main(["--env-file", str(env), "--state-dir", str(state), "--repo-root", str(root), "--skip-template-domain"])
            self.assertEqual(2, code)
            self.assertNotIn(secret, stderr.getvalue())

    def test_caddy_placeholder_requires_explicit_packaging_only_override(self):
        with tempfile.TemporaryDirectory() as raw:
            root = Path(raw)
            for relative in ("deploy/gunicorn.conf.py", "deploy/saathi.service", "backend/hosted.py"):
                candidate = root / relative; candidate.parent.mkdir(parents=True, exist_ok=True); candidate.write_text("x")
            caddy = root / "deploy/Caddyfile"; caddy.write_text("saathi.example.invalid")
            with self.assertRaises(preflight.PreflightError):
                preflight.validate_template(root)


if __name__ == "__main__":
    unittest.main()
