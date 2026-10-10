package com.saathi.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.saathi.core.UiNode

/** Editable value getters are not accessed. Static-content filtering remains best effort. */
object NodeMasker {

    private data class Inherited(val clickable: Rect? = null, val parent: Int? = null, val selection: Boolean = false)

    fun flatten(root: AccessibilityNodeInfo, isCurrent: () -> Boolean = { true }): List<UiNode> {
        val nodes = mutableListOf<UiNode>()
        CompleteTreeWalk.visit(root, Inherited(), { it.childCount }, { node, index -> node.getChild(index) },
            { node -> runCatching { node.recycle() }; Unit }, { node, ancestor -> read(node, nodes, ancestor) },
            isCurrent = isCurrent)
        // A private message/document need not contain digits. Discard the local copied content
        // as soon as the screen context is classified; keep only a non-content handoff flag.
        return if (com.saathi.core.PrivateContextPolicy.blocksCloud(nodes)) nodes.map {
            it.copy(text = null, description = null, hint = null, resourceId = null,
                hasValue = false, valueKnown = false, privateContext = true,
                contentInvalid = false, requiredField = false, inputType = 0, privacyKind = PrivacyKind.UNKNOWN_SENSITIVE)
        } else nodes
    }

    private fun read(node: AccessibilityNodeInfo, into: MutableList<UiNode>, inherited: Inherited): Inherited {
        val bounds = Rect().also(node::getBoundsInScreen)
        val className = node.className?.toString()
        val formControl = com.saathi.core.FormControlKind.from(className, node.isCheckable,
            node.extras.getCharSequence("AccessibilityNodeInfo.chromeRole")?.toString())
        val selection = inherited.selection || formControl == com.saathi.core.FormControlKind.DROPDOWN
        var parent = inherited.parent
        if (node.isVisibleToUser && !bounds.isEmpty) {
            val associated = if (inherited.selection) null else associatedLabel(node, formControl)
            val hint = if (inherited.selection) null else node.hintText?.toString()?.take(300)?.takeIf { it.isNotBlank() } ?: associated?.text
            val id = node.viewIdResourceName
            val editable = node.isEditable || className.orEmpty().contains("EditText")
            val content = NodeContentPolicy.read(node.isPassword || associated?.sensitive == true, editable, hint, id, node.inputType,
                node.isShowingHintText, { node.text?.toString() }, { node.contentDescription?.toString() }, node.textSelectionEnd, selection)
            val sensitive = content.sensitive
            parent = into.size
            into += UiNode(
                bounds = bounds,
                text = content.text,
                description = content.description,
                hint = if (sensitive) null else hint,
                resourceId = if (sensitive) null else id,
                className = className,
                isPassword = node.isPassword,
                isEnabled = node.isEnabled,
                isClickable = node.isClickable,
                isSensitive = sensitive,
                hasValue = content.hasValue,
                valueKnown = content.valueKnown,
                structuralPrivateField = content.structuralPrivateField,
                clickableAncestorBounds = inherited.clickable?.let(::Rect),
                parentIndex = inherited.parent,
                privacyKind = PrivacyClassification.classify(node.isPassword, editable, node.inputType, hint, id, sensitive,
                    node.isClickable || inherited.clickable != null, content.text != null || content.description != null),
                isEditable = editable,
                isFocused = node.isFocused,
                contentInvalid = !sensitive && node.isContentInvalid,
                // Optional absence of new platform metadata remains UNKNOWN, not OPTIONAL.
                requiredField = !sensitive && android.os.Build.VERSION.SDK_INT >= 36 &&
                    runCatching { AccessibilityNodeInfo::class.java.getMethod("isFieldRequired").invoke(node) == true }.getOrDefault(false),
                inputType = if (sensitive) 0 else node.inputType,
                formControl = if (inherited.selection) com.saathi.core.FormControlKind.NONE else formControl
            )
        }
        return Inherited(if (node.isClickable && node.isEnabled && node.isVisibleToUser && !bounds.isEmpty) bounds else inherited.clickable,
            parent, inherited.selection || formControl != com.saathi.core.FormControlKind.NONE)
    }

    /** Use an explicit label relationship, never a selected value or nearby-text guess. */
    private fun associatedLabel(node: AccessibilityNodeInfo, kind: com.saathi.core.FormControlKind): NodeContentPolicy.Content? {
        if (!node.isEditable && kind == com.saathi.core.FormControlKind.NONE) return null
        // Some providers (and unsealed synthetic nodes) cannot resolve this optional
        // relationship. Keep the field unnamed; never fall back to its entered value.
        @Suppress("DEPRECATION") val label = runCatching { node.labeledBy }.getOrNull() ?: return null
        return try {
            if (!label.isVisibleToUser || label.isEditable || label.isPassword || label.className?.toString()?.contains("EditText") == true) null
            else NodeContentPolicy.read(label.isPassword, false, null, label.viewIdResourceName, label.inputType,
                false, { label.text?.toString() }, { null }, selection = com.saathi.core.FormControlKind.from(
                    label.className?.toString(), label.isCheckable,
                    label.extras.getCharSequence("AccessibilityNodeInfo.chromeRole")?.toString()) != com.saathi.core.FormControlKind.NONE)
        } finally { @Suppress("DEPRECATION") label.recycle() }
    }

    fun isSensitive(isPassword: Boolean, vararg values: String?): Boolean =
        SensitiveContent.isSensitive(isPassword, *values)
}
