package com.ku_stacks.ku_ring.data.mapper

import com.ku_stacks.ku_ring.data.db.DepartmentEntity
import com.ku_stacks.ku_ring.data.db.NoticeEntity
import com.ku_stacks.ku_ring.domain.Department
import com.ku_stacks.ku_ring.domain.Notice

fun Notice.toEntity(): NoticeEntity = NoticeEntity(
    articleId = articleId,
    category = category,
    department = department,
    subject = concatSubjectAndTag(subject, tag),
    postedDate = postedDate,
    url = url,
    isNew = isNew,
    isRead = isRead,
    isSaved = isSaved,
    isReadOnStorage = isReadOnStorage,
)

fun List<Department>.toEntityList(): List<DepartmentEntity> = map { it.toEntity() }

fun Department.toEntity(): DepartmentEntity = DepartmentEntity(
    name = name,
    shortName = shortName,
    koreanName = koreanName,
    isSubscribed = isSubscribed,
)