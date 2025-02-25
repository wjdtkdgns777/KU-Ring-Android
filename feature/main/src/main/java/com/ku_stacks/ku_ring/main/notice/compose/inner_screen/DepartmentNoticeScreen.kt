package com.ku_stacks.ku_ring.main.notice.compose.inner_screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ku_stacks.ku_ring.designsystem.components.KuringCallToAction
import com.ku_stacks.ku_ring.designsystem.components.LazyPagingNoticeItemColumn
import com.ku_stacks.ku_ring.designsystem.kuringtheme.KuringTheme
import com.ku_stacks.ku_ring.designsystem.kuringtheme.values.Pretendard
import com.ku_stacks.ku_ring.domain.Department
import com.ku_stacks.ku_ring.domain.Notice
import com.ku_stacks.ku_ring.main.R
import com.ku_stacks.ku_ring.main.notice.DepartmentNoticeScreenState
import com.ku_stacks.ku_ring.main.notice.DepartmentNoticeViewModel
import com.ku_stacks.ku_ring.main.notice.compose.LocalKuringBotFabState
import com.ku_stacks.ku_ring.main.notice.compose.components.DepartmentHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DepartmentNoticeScreen(
    viewModel: DepartmentNoticeViewModel,
    onNoticeClick: (Notice) -> Unit,
    onNavigateToEditDepartment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedDepartments by viewModel.subscribedDepartments.collectAsStateWithLifecycle()
    val noticesFlow by viewModel.currentDepartmentNotice.collectAsStateWithLifecycle()
    val notices = noticesFlow?.collectAsLazyPagingItems()

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            notices?.refresh()
            isRefreshing = false
        },
        refreshThreshold = 75.dp,
    )

    val departmentNoticeScreenState by viewModel.departmentNoticeScreenState.collectAsStateWithLifecycle()

    when (departmentNoticeScreenState) {
        DepartmentNoticeScreenState.InitialLoading -> {
            Box(modifier = modifier) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        DepartmentNoticeScreenState.DepartmentsEmpty -> {
            DepartmentEmptyScreen(
                onNavigateToEditDepartment = onNavigateToEditDepartment,
                modifier = modifier,
            )
        }

        DepartmentNoticeScreenState.DepartmentsNotEmpty -> {
            DepartmentNoticeScreen(
                selectedDepartments = selectedDepartments,
                onSelectDepartment = viewModel::selectDepartment,
                onNavigateToEditDepartment = onNavigateToEditDepartment,
                notices = notices,
                onNoticeClick = onNoticeClick,
                isRefreshing = isRefreshing,
                refreshState = refreshState,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun DepartmentEmptyScreen(
    onNavigateToEditDepartment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.department_screen_add_department_caption),
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 24.45.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                color = KuringTheme.colors.textCaption1,
                textAlign = TextAlign.Center,
            ),
        )
        KuringCallToAction(
            onClick = onNavigateToEditDepartment,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus_plain_v2),
                contentDescription = null,
                tint = KuringTheme.colors.background,
                modifier = Modifier.padding(end = 4.dp),
            )
            Text(
                text = stringResource(id = R.string.department_screen_add_department_button),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = Pretendard,
                    fontWeight = FontWeight(600),
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DepartmentNoticeScreen(
    selectedDepartments: List<Department>,
    onSelectDepartment: (Department) -> Unit,
    onNavigateToEditDepartment: () -> Unit,
    notices: LazyPagingItems<Notice>?,
    onNoticeClick: (Notice) -> Unit,
    isRefreshing: Boolean,
    refreshState: PullRefreshState,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = tween(durationMillis = 250)
    )

    val kuringBotFabState = LocalKuringBotFabState.current

    val isOpening by remember { derivedStateOf { sheetState.targetValue != ModalBottomSheetValue.Hidden } }
    val shouldFabVisible = !sheetState.isVisible && !isOpening
    LaunchedEffect(shouldFabVisible) {
        if (shouldFabVisible) {
            kuringBotFabState.show()
        } else {
            kuringBotFabState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            DepartmentSelectorBottomSheet(
                departments = selectedDepartments,
                onSelect = {
                    onSelectDepartment(it)
                    scope.launch {
                        sheetState.hide()
                    }
                },
                onNavigateToEditDepartment = onNavigateToEditDepartment,
                modifier = Modifier
                    .background(KuringTheme.colors.background)
                    .fillMaxWidth(),
            )
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = modifier,
    ) {
        val selectedDepartment = selectedDepartments.firstOrNull { it.isSelected }
        DepartmentNoticeScreenContent(
            selectedDepartment = selectedDepartment,
            sheetState = sheetState,
            refreshState = refreshState,
            notices = notices,
            onNoticeClick = onNoticeClick,
            isRefreshing = isRefreshing,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DepartmentNoticeScreenContent(
    selectedDepartment: Department?,
    sheetState: ModalBottomSheetState,
    refreshState: PullRefreshState,
    notices: LazyPagingItems<Notice>?,
    onNoticeClick: (Notice) -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            DepartmentHeader(
                selectedDepartmentName = selectedDepartment?.koreanName ?: "",
                onClick = { scope.launch { sheetState.show() } },
            )
            LazyPagingNoticeItemColumn(
                notices = notices,
                onNoticeClick = onNoticeClick,
                modifier = Modifier.pullRefresh(refreshState),
                noticeFilter = { !it.isImportant },
            )
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}