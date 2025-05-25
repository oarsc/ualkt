package org.oar.ualkt.model

data class FirefoxBookmark(
    val guid: String,
    val title: String,
    val index: Int,
    val dateAdded: Long,
    val lastModified: Long,
    val id: Int,
    val typeCode: Int,
    val type: String, // 'text/x-moz-place' | 'text/x-moz-place-container',
    val root: String?, //'bookmarksMenuFolder' | 'mobileFolder' | 'toolbarFolder' | 'unfiledBookmarksFolder',
    val iconUri: String? = null,
    val children: List<FirefoxBookmark> = emptyList(),
    val uri: String? = null,
)