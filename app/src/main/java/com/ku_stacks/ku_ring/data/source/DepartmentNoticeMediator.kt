package com.ku_stacks.ku_ring.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ku_stacks.ku_ring.data.api.NoticeClient
import com.ku_stacks.ku_ring.data.api.response.DepartmentNoticeResponse
import com.ku_stacks.ku_ring.data.db.KuRingDatabase
import com.ku_stacks.ku_ring.data.db.NoticeEntity
import com.ku_stacks.ku_ring.data.db.PageKeyEntity
import com.ku_stacks.ku_ring.data.mapper.toEntityList
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class DepartmentNoticeMediator(
    private val shortName: String,
    private val noticeClient: NoticeClient,
    private val database: KuRingDatabase,
) : RemoteMediator<Int, NoticeEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NoticeEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> getRefreshKey(state) ?: 0
            LoadType.PREPEND -> getPrependKey(state)
            LoadType.APPEND -> getAppendKey(state)
        }
        Timber.d("Load dept notices: $shortName, $loadType, $page")

        if (page == null || page < 0) {
            Timber.d("Dept notices skip: $shortName, $loadType, $page")
            return MediatorResult.Success(endOfPaginationReached = page != null)
        }

        return try {
            clearNoticesWhenRefresh(loadType)

            val noticeResponse = noticeClient.fetchDepartmentNoticeList(
                shortName = shortName,
                page = page,
                size = itemSize
            )
            Timber.d("Loaded dept notices: ${noticeResponse.data.lastOrNull()?.articleId}")
            insertNotices(noticeResponse.data, page)

            val isPageEnd = noticeResponse.data.isEmpty()
            if (isPageEnd) {
                Timber.d("Dept notices page end: $shortName, $loadType, $page")
            }
            MediatorResult.Success(endOfPaginationReached = isPageEnd)
        } catch (e: Exception) {
            Timber.e("Dept notices exception: ${e.message}")
            MediatorResult.Error(e)
        }
    }

    private suspend fun insertNotices(notices: List<DepartmentNoticeResponse>, page: Int) {
        val noticeEntities = notices.toEntityList(shortName)
        val pageKeyEntities = noticeEntities.map {
            PageKeyEntity(articleId = it.articleId, page = page)
        }
        database.withTransaction {
            database.noticeDao().insertDepartmentNotices(noticeEntities)
            database.pageKeyDao().insertPageKeys(pageKeyEntities)
        }
    }

    private suspend fun clearNoticesWhenRefresh(loadType: LoadType) {
        if (loadType == LoadType.REFRESH) {
            database.withTransaction {
                database.noticeDao().clearDepartment(shortName)
            }
        }
    }

    private suspend fun getRefreshKey(state: PagingState<Int, NoticeEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.articleId?.let { articleId ->
                database.pageKeyDao().getPageKey(articleId)?.page
            }
        }
    }

    private suspend fun getPrependKey(state: PagingState<Int, NoticeEntity>): Int? {
        val firstItem = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
        return firstItem?.let {
            database.pageKeyDao().getPageKey(firstItem.articleId)?.page?.minus(1)
        }
    }

    private suspend fun getAppendKey(state: PagingState<Int, NoticeEntity>): Int? {
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
        return lastItem?.let {
            database.pageKeyDao().getPageKey(lastItem.articleId)?.page?.plus(1)
        }
    }

    companion object {
        const val itemSize = 20
    }
}