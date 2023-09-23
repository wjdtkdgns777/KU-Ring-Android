package com.ku_stacks.ku_ring.notice.api.response

import com.google.gson.annotations.SerializedName

data class NoticeListResponse(
    @SerializedName(value = "message")
    val resultMsg: String,
    @SerializedName(value = "code")
    val resultCode: Int,
    @SerializedName(value = "data")
    val noticeResponse: List<NoticeResponse>,
) {
    val isSuccess: Boolean
        get() = (resultCode == 200)

    companion object {
        fun mock() = NoticeListResponse(
            resultMsg = "성공",
            resultCode = 200,
            noticeResponse = listOf(NoticeResponse.mock()),
        )
    }
}