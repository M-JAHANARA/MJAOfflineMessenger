package com.mja.messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mja.messenger.ui.theme.*

/* ------------------------------------------------------------------ */
/* Buttons                                                             */
/* ------------------------------------------------------------------ */

enum class MjaButtonStyle { Primary, Secondary, Text, Destructive, Ghost }

/**
 * The single button of MJA. Height 52dp, radius 14dp.
 */
@Composable
fun MjaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MjaButtonStyle = MjaButtonStyle.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
) {
    val bg = when (style) {
        MjaButtonStyle.Primary -> MjaBrand
        MjaButtonStyle.Secondary -> MjaElevated
        MjaButtonStyle.Destructive -> MjaError.copy(alpha = 0.16f)
        MjaButtonStyle.Text -> Color.Transparent
        MjaButtonStyle.Ghost -> MjaCard
    }
    val fg = when (style) {
        MjaButtonStyle.Primary -> MjaBackground
        MjaButtonStyle.Destructive -> MjaError
        else -> MjaTextPrimary
    }
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(bg)
            .border(
                width = if (style == MjaButtonStyle.Destructive) 1.dp else 0.dp,
                color = if (style == MjaButtonStyle.Destructive) MjaError.copy(alpha = 0.45f) else Color.Transparent,
                shape = shape,
            )
            .clickable(enabled = enabled && !loading) { onClick() }
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = fg,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(10.dp))
            } else if (icon != null) {
                Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(if (enabled) 1f else 0.5f),
            )
        }
    }
}

/** Compact square icon button used in top bars and composers. */
@Composable
fun MjaIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = MjaTextSecondary,
    background: Color = Color.Transparent,
    size: Dp = 40.dp,
    iconSize: Dp = 21.dp,
    bordered: Boolean = false,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                width = if (bordered) 1.dp else 0.dp,
                color = if (bordered) MjaBorder else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/* ------------------------------------------------------------------ */
/* Text fields                                                         */
/* ------------------------------------------------------------------ */

@Composable
fun MjaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    minHeight: Dp = 52.dp,
) {
    val borderColor = when {
        isError -> MjaError
        !enabled -> MjaBorder
        else -> MjaBorder
    }

    Column(modifier = modifier) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MjaTextSecondary,
                modifier = Modifier.padding(bottom = 6.dp, start = 2.dp),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(if (enabled) MjaCard else MjaBackground2)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon, contentDescription = null,
                    tint = if (isError) MjaError else MjaTextDisabled,
                    modifier = Modifier.size(19.dp),
                )
                Spacer(Modifier.width(10.dp))
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder.isNotBlank()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MjaTextDisabled,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = singleLine,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = if (enabled) MjaTextPrimary else MjaTextDisabled,
                    ),
                    cursorBrush = SolidColor(MjaBrand),
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                    keyboardActions = KeyboardActions(onDone = { onImeAction() }, onGo = { onImeAction() }, onSend = { onImeAction() }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                trailingIcon()
            }
        }
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isError) MjaError else MjaTextSecondary,
                modifier = Modifier.padding(top = 6.dp, start = 2.dp),
            )
        }
    }
}

/** Rounded search field used on Home / Contacts / Groups / Search screens. */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "جست‌وجو",
    onImeAction: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MjaCard)
            .border(1.dp, MjaBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons_Search, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(9.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MjaTextDisabled)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MjaTextPrimary),
                cursorBrush = SolidColor(MjaBrand),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onImeAction() }),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons_Close, contentDescription = "پاک کردن",
                tint = MjaTextDisabled,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onValueChange("") },
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/* Tabs / chips / rows                                                 */
/* ------------------------------------------------------------------ */

/** Underlined segmented tab bar (همه / خصوصی / گروه‌ها / ...). */
@Composable
fun MjaTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MjaBackground2)
            .padding(4.dp),
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) MjaElevated else Color.Transparent)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) MjaTextPrimary else MjaTextSecondary,
                )
            }
        }
    }
}

@Composable
fun MjaChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) MjaElevated else MjaCard)
            .border(1.dp, if (selected) MjaAccent.copy(alpha = 0.5f) else MjaBorder, RoundedCornerShape(11.dp))
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                icon, contentDescription = null,
                tint = if (selected) MjaTextPrimary else MjaTextSecondary,
                modifier = Modifier.size(15.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) MjaTextPrimary else MjaTextSecondary,
        )
    }
}

/** Settings / list row with icon, title, optional subtitle & trailing. */
@Composable
fun SettingsRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
    value: String? = null,
    danger: Boolean = false,
    enabled: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
) {
    val tint = if (danger) MjaError else MjaTextSecondary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (danger) MjaError.copy(alpha = 0.12f) else MjaElevated),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (danger) MjaError else MjaTextPrimary,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MjaTextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (value != null) {
            Text(value, style = MaterialTheme.typography.bodySmall, color = MjaTextDisabled)
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        } else if (value == null) {
            Icon(Icons_Chevron, contentDescription = null, tint = MjaTextDisabled, modifier = Modifier.size(18.dp))
        }
    }
}

/** Switch row used across Settings / Privacy / Notifications. */
@Composable
fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MjaElevated),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = MjaTextSecondary, modifier = Modifier.size(17.dp))
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MjaTextPrimary)
            if (description != null) {
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MjaTextSecondary,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MjaBackground,
                checkedTrackColor = MjaBrand,
                uncheckedThumbColor = MjaTextDisabled,
                uncheckedTrackColor = MjaElevated,
                uncheckedBorderColor = MjaBorder,
            ),
        )
    }
}

/** Small caps label with a colored dot — «آنلاین» / «آفلاین» / وضعیت شبکه. */
@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MjaOnline,
    filled: Boolean = false,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(if (filled) color.copy(alpha = 0.14f) else MjaElevated)
            .border(1.dp, if (filled) color.copy(alpha = 0.4f) else MjaBorder, RoundedCornerShape(9.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = if (filled) color else MjaTextSecondary)
    }
}

/* ------------------------------------------------------------------ */
/* English / IP / ID text inside an RTL layout                         */
/* ------------------------------------------------------------------ */

/** Renders latin/ID/IP strings with a guaranteed LTR direction. */
@Composable
fun LtrText(
    text: String,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MjaTextSecondary,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(
            text = text,
            style = style,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
            modifier = modifier,
        )
    }
}
