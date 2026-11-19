package com.mja.messenger.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forward
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

/* ------------------------------------------------------------------ */
/* Icon aliases — one shared vocabulary for every MJA screen.          */
/* ------------------------------------------------------------------ */

val Icons_Search: ImageVector get() = Icons.Default.Search
val Icons_Close: ImageVector get() = Icons.Default.Close
val Icons_Chevron: ImageVector get() = Icons.Default.KeyboardArrowLeft
val Icons_ChevronBack: ImageVector get() = Icons.Default.KeyboardArrowRight
val Icons_Back: ImageVector get() = Icons.AutoMirrored.Filled.ArrowBack
val Icons_More: ImageVector get() = Icons.Default.MoreVert
val Icons_Send: ImageVector get() = Icons.AutoMirrored.Filled.Send
val Icons_Mic: ImageVector get() = Icons.Default.Mic
val Icons_MicOff: ImageVector get() = Icons.Default.MicOff
val Icons_Emoji: ImageVector get() = Icons.Default.Face
val Icons_Attach: ImageVector get() = Icons.Default.AttachFile
val Icons_Camera: ImageVector get() = Icons.Default.CameraAlt
val Icons_Gallery: ImageVector get() = Icons.Default.PhotoLibrary
val Icons_Image: ImageVector get() = Icons.Default.Photo
val Icons_Video: ImageVector get() = Icons.Default.Videocam
val Icons_VideoOff: ImageVector get() = Icons.Default.VideocamOff
val Icons_Play: ImageVector get() = Icons.Default.PlayArrow
val Icons_Pause: ImageVector get() = Icons.Default.Pause
val Icons_Person: ImageVector get() = Icons.Default.Person
val Icons_People: ImageVector get() = Icons.Default.People
val Icons_Group: ImageVector get() = Icons.Default.Group
val Icons_Groups: ImageVector get() = Icons.Default.Groups
val Icons_Notifications: ImageVector get() = Icons.Default.Notifications
val Icons_Settings: ImageVector get() = Icons.Default.Settings
val Icons_Home: ImageVector get() = Icons.Default.Home
val Icons_Wifi: ImageVector get() = Icons.Default.Wifi
val Icons_WifiOff: ImageVector get() = Icons.Default.WifiOff
val Icons_WifiOutlined: ImageVector get() = Icons.Outlined.Wifi
val Icons_Add: ImageVector get() = Icons.Default.Add
val Icons_AddPerson: ImageVector get() = Icons.Default.PersonAdd
val Icons_Call: ImageVector get() = Icons.Default.Call
val Icons_CallEnd: ImageVector get() = Icons.Default.CallEnd
val Icons_Volume: ImageVector get() = Icons.Default.VolumeUp
val Icons_Speaker: ImageVector get() = Icons.Default.Cast
val Icons_SwitchCam: ImageVector get() = Icons.Default.SwapHoriz
val Icons_Phone: ImageVector get() = Icons.Default.Phone
val Icons_Check: ImageVector get() = Icons.Default.Check
val Icons_CheckCircle: ImageVector get() = Icons.Default.CheckCircle
val Icons_Done: ImageVector get() = Icons.Default.Done
val Icons_Schedule: ImageVector get() = Icons.Default.Schedule
val Icons_Error: ImageVector get() = Icons.Default.Error
val Icons_ErrorOutline: ImageVector get() = Icons.Default.ErrorOutline
val Icons_CloudOff: ImageVector get() = Icons.Default.CloudOff
val Icons_Sync: ImageVector get() = Icons.Default.Sync
val Icons_Refresh: ImageVector get() = Icons.Default.Sync
val Icons_Folder: ImageVector get() = Icons.Default.FileCopy
val Icons_File: ImageVector get() = Icons.AutoMirrored.Filled.InsertDriveFile
val Icons_FileDoc: ImageVector get() = Icons.Default.Description
val Icons_FileAudio: ImageVector get() = Icons.Default.Audiotrack
val Icons_FileVideo: ImageVector get() = Icons.Default.VideoFile
val Icons_FileImage: ImageVector get() = Icons.Default.Image
val Icons_Delete: ImageVector get() = Icons.Default.DeleteOutline
val Icons_DeleteSolid: ImageVector get() = Icons.Default.Delete
val Icons_Info: ImageVector get() = Icons.Default.Info
val Icons_InfoOutlined: ImageVector get() = Icons.Outlined.Info
val Icons_Help: ImageVector get() = Icons.Default.HelpOutline
val Icons_Filter: ImageVector get() = Icons.Default.FilterList
val Icons_Block: ImageVector get() = Icons.Default.Block
val Icons_Report: ImageVector get() = Icons.Default.Report
val Icons_Pin: ImageVector get() = Icons.Default.PushPin
val Icons_Reply: ImageVector get() = Icons.Default.Reply
val Icons_Forward: ImageVector get() = Icons.Default.Forward
val Icons_Copy: ImageVector get() = Icons.Default.CopyAll
val Icons_Edit: ImageVector get() = Icons.Default.Edit
val Icons_Logout: ImageVector get() = Icons.Default.Logout
val Icons_Language: ImageVector get() = Icons.Default.Language
val Icons_Palette: ImageVector get() = Icons.Default.Palette
val Icons_DarkMode: ImageVector get() = Icons.Default.DarkMode
val Icons_Backup: ImageVector get() = Icons.Default.Save
val Icons_Security: ImageVector get() = Icons.Default.Shield
val Icons_Key: ImageVector get() = Icons.Default.Lock
val Icons_Shield: ImageVector get() = Icons.Default.Shield
val Icons_Lock: ImageVector get() = Icons.Default.Lock
val Icons_Storage: ImageVector get() = Icons.Default.Storage
val Icons_TextSize: ImageVector get() = Icons.Default.Title
val Icons_Animations: ImageVector get() = Icons.Default.Bolt
val Icons_History: ImageVector get() = Icons.Default.History
val Icons_Upload: ImageVector get() = Icons.Default.Upload
val Icons_Qr: ImageVector get() = Icons.Default.QrCode
val Icons_Device: ImageVector get() = Icons.Default.Smartphone
val Icons_Speed: ImageVector get() = Icons.Default.Speed
val Icons_Signal: ImageVector get() = Icons.Default.SignalCellularAlt
val Icons_Terminal: ImageVector get() = Icons.Default.Terminal
val Icons_Link: ImageVector get() = Icons.Default.Link
val Icons_Menu: ImageVector get() = Icons.Default.Menu
val Icons_MoreItems: ImageVector get() = Icons.Default.MoreHoriz
val Icons_Chat: ImageVector get() = Icons.Default.Chat
val Icons_Email: ImageVector get() = Icons.Default.Email
val Icons_NotificationsOff: ImageVector get() = Icons.Default.NotificationsOff
val Icons_SmartDisplay: ImageVector get() = Icons.Default.Cast
val Icons_Star: ImageVector get() = Icons.Default.Favorite
val Icons_Avatar: ImageVector get() = Icons.Default.AccountCircle
val Icons_Exit: ImageVector get() = Icons.Default.ExitToApp
val Icons_Touch: ImageVector get() = Icons.Default.TouchApp
val Icons_AutoMode: ImageVector get() = Icons.Default.Build
val Icons_HomeOutlined: ImageVector get() = Icons.Default.Home
val Icons_Share: ImageVector get() = Icons.Default.Share
val Icons_PersonOutline: ImageVector get() = Icons.Outlined.AccountCircle
val Icons_HistoryEdu: ImageVector get() = Icons.Default.History
val Icons_LocalLibrary: ImageVector get() = Icons.Default.Title
val Icons_SearchPerson: ImageVector get() = Icons.Default.Search
val Icons_List: ImageVector get() = Icons.AutoMirrored.Outlined.List
val Icons_NotificationBell: ImageVector get() = Icons.Default.Notifications
val Icons_MicNone: ImageVector get() = Icons.Default.Mic
val Icons_Made: ImageVector get() = Icons.Default.CallMade
val Icons_Keyboard: ImageVector get() = Icons.Default.Email
val Icons_Grid: ImageVector get() = Icons.Default.PhotoLibrary
val Icons_Trash: ImageVector get() = Icons.Default.Delete
val Icons_ClearAll: ImageVector get() = Icons.Default.Close
val Icons_Members: ImageVector get() = Icons.Default.Groups
val Icons_Admin: ImageVector get() = Icons.Default.Star
val Icons_ArrowUp: ImageVector get() = Icons.Default.ArrowUpward
val Icons_Step: ImageVector get() = Icons.Default.KeyboardArrowRight
val Icons_Nearby: ImageVector get() = Icons.Default.Cast
val Icons_Queue: ImageVector get() = Icons.Default.Schedule
val Icons_Dashboard: ImageVector get() = Icons.Default.Speed
val Icons_Server: ImageVector get() = Icons.Default.Terminal
val Icons_TextSnippet: ImageVector get() = Icons.Default.Description
val Icons_RemoveUser: ImageVector get() = Icons.Default.Person
val Icons_Bell: ImageVector get() = Icons.Default.Notifications
val Icons_Visibility: ImageVector get() = Icons.Default.Visibility
val Icons_VisibilityOff: ImageVector get() = Icons.Default.VisibilityOff
