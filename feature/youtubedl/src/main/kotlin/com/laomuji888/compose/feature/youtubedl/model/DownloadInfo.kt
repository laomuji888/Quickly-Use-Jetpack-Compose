package com.laomuji888.compose.feature.youtubedl.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.laomuji888.compose.core.logic.database.entity.YoutubeDLInfoEntity
import com.laomuji888.compose.res.R

/**
 * @param url 下载的链接
 * @param title 下载的标题
 * @param thumbnail 缩略图
 * @param isError 是否有错误
 * @param isDone 是否已经完成
 * @param duration 时长,秒
 * @param fileSize 文件大小, 使用[getFileSizeText]获取
 * @param progress 进度, 百分比
 * @param filename 文件名, 唯一
 * @param isDownloading 是否正在下载
 */
data class DownloadInfo(
    val url: String,
    val title: String,
    val thumbnail: String? = null,
    val isError: Boolean = false,
    val isDone: Boolean = false,
    val duration: Double = 0.0,
    val fileSize: Double = 0.0,
    val progress: Float = 0.0f,
    val filename: String = "",
    val isDownloading: Boolean = false
){
    companion object{
        private const val GB = 1024 * 1024 * 1024
        private const val MB = 1024 * 1024
        fun DownloadInfo.toYoutubeDLInfoEntity(): YoutubeDLInfoEntity{
            return YoutubeDLInfoEntity(
                url = url,
                title = title,
                thumbnail = thumbnail,
                isError = isError,
                isDone = isDone,
                duration = duration,
                fileSize = fileSize,
                progress = progress,
                isDownloading = isDownloading
            ).apply {
                filename = this@toYoutubeDLInfoEntity.filename
            }
        }
        fun List<YoutubeDLInfoEntity>.toDownloadInfoList(): List<DownloadInfo>{
            return map {
                DownloadInfo(
                    url = it.url,
                    title = it.title,
                    thumbnail = it.thumbnail,
                    isError = it.isError,
                    isDone = it.isDone,
                    duration = it.duration,
                    fileSize = it.fileSize,
                    progress = it.progress,
                    filename = it.filename,
                    isDownloading = it.isDownloading
                )
            }
        }
    }
    @Composable
    fun getDurationText(): String{
        val minute = duration / 60
        val second = duration % 60
        return stringResource(R.string.string_youtubedl_screen_duration_format, minute.toInt(), second.toInt())
    }
    fun getFileSizeText(): String{
        return if (fileSize > GB) "%.2f GB".format(fileSize / GB)
        else "%.2f MB".format(fileSize / MB)
    }
    fun getProgressText(): String{
        if(progress < 0){
            return "0%"
        }
        return "${progress.toInt()}%"
    }
}

