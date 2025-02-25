package com.ku_stacks.ku_ring.edit_subscription.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ku_stacks.ku_ring.designsystem.components.KuringCallToAction
import com.ku_stacks.ku_ring.designsystem.components.LightAndDarkPreview
import com.ku_stacks.ku_ring.designsystem.components.LightPreview
import com.ku_stacks.ku_ring.designsystem.components.topbar.CenterTitleTopBar
import com.ku_stacks.ku_ring.designsystem.kuringtheme.KuringTheme
import com.ku_stacks.ku_ring.designsystem.kuringtheme.values.Pretendard
import com.ku_stacks.ku_ring.edit_subscription.EditSubscriptionTab
import com.ku_stacks.ku_ring.edit_subscription.EditSubscriptionViewModel
import com.ku_stacks.ku_ring.edit_subscription.R
import com.ku_stacks.ku_ring.edit_subscription.compose.components.DepartmentSubscriptionItem
import com.ku_stacks.ku_ring.edit_subscription.compose.components.NormalSubscriptionItem
import com.ku_stacks.ku_ring.edit_subscription.uimodel.DepartmentSubscriptionUiModel
import com.ku_stacks.ku_ring.edit_subscription.uimodel.NormalSubscriptionUiModel
import kotlinx.coroutines.launch

@Composable
fun EditSubscriptionScreen(
    onNavigateToBack: () -> Unit,
    onAddDepartmentButtonClick: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditSubscriptionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditSubscriptionScreen(
        categories = uiState.categories,
        departments = uiState.departments,
        onNavigateToBack = onNavigateToBack,
        onCategoryClick = viewModel::onNormalSubscriptionItemClick,
        onDepartmentClick = viewModel::onDepartmentSubscriptionItemClick,
        onAddDepartmentButtonClick = onAddDepartmentButtonClick,
        onSubscriptionComplete = {
            if (viewModel.isInitialLoadDone) {
                viewModel.saveSubscribe()
                onFinish()
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun EditSubscriptionScreen(
    categories: List<NormalSubscriptionUiModel>,
    departments: List<DepartmentSubscriptionUiModel>,
    onNavigateToBack: () -> Unit,
    onCategoryClick: (Int) -> Unit,
    onDepartmentClick: (String) -> Unit,
    onAddDepartmentButtonClick: () -> Unit,
    onSubscriptionComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(KuringTheme.colors.background)
    ) {
        CenterTitleTopBar(
            title = stringResource(id = R.string.app_bar_title),
            action = stringResource(id = R.string.app_bar_action),
            onActionClick = onSubscriptionComplete,
            actionClickLabel = stringResource(id = R.string.department_subscription_complete),
            navigation = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_v2),
                    contentDescription = stringResource(id = R.string.app_bar_navigation),
                    tint = KuringTheme.colors.gray600,
                )
            },
            onNavigationClick = onNavigateToBack,
        )
        SubscriptionTitle(modifier = Modifier.padding(start = 32.dp, top = 30.dp))
        SubscriptionTabs(
            categories = categories,
            departments = departments,
            onCategoryClick = onCategoryClick,
            onDepartmentClick = onDepartmentClick,
            onAddDepartmentButtonClick = onAddDepartmentButtonClick,
            onSubscriptionComplete = onSubscriptionComplete,
            modifier = Modifier
                .padding(top = 68.dp)
                .weight(1f),
        )
    }
}

@Composable
private fun SubscriptionTitle(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = R.string.title),
        style = TextStyle(
            fontSize = 24.sp,
            lineHeight = 36.sp,
            fontFamily = Pretendard,
            fontWeight = FontWeight(700),
            color = KuringTheme.colors.textTitle,
        ),
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubscriptionTabs(
    categories: List<NormalSubscriptionUiModel>,
    departments: List<DepartmentSubscriptionUiModel>,
    onCategoryClick: (Int) -> Unit,
    onDepartmentClick: (String) -> Unit,
    onAddDepartmentButtonClick: () -> Unit,
    onSubscriptionComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { EditSubscriptionTab.values().size }
    )

    val currentPage = pagerState.currentPage
    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = currentPage,
            backgroundColor = KuringTheme.colors.background,
            contentColor = KuringTheme.colors.mainPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[currentPage])
                        .padding(horizontal = 29.dp)
                )
            }
        ) {
            EditSubscriptionTab.values().forEachIndexed { index, tab ->
                SubscriptionTab(
                    tab = tab,
                    isSelected = index == currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
        SubscriptionPager(
            categories = categories,
            onCategoryClick = onCategoryClick,
            departments = departments,
            onDepartmentClick = onDepartmentClick,
            onAddDepartmentButtonClick = onAddDepartmentButtonClick,
            modifier = Modifier.weight(1f),
            onSubscriptionComplete = onSubscriptionComplete,
            pagerState = pagerState,
        )
    }
}

@Composable
private fun SubscriptionTab(
    tab: EditSubscriptionTab,
    isSelected: Boolean,
    onClick: (EditSubscriptionTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Tab(
        selected = isSelected,
        onClick = { onClick(tab) },
        modifier = modifier,
        selectedContentColor = KuringTheme.colors.mainPrimary,
        unselectedContentColor = KuringTheme.colors.textCaption1,
    ) {
        Crossfade(
            targetState = isSelected,
            label = "subscription tab"
        ) { isSelected ->
            Text(
                text = stringResource(id = tab.tabTitleId),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontFamily = Pretendard,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                ),
                modifier = Modifier.padding(horizontal = 27.dp, vertical = 14.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubscriptionPager(
    categories: List<NormalSubscriptionUiModel>,
    onCategoryClick: (Int) -> Unit,
    departments: List<DepartmentSubscriptionUiModel>,
    onDepartmentClick: (String) -> Unit,
    onAddDepartmentButtonClick: () -> Unit,
    onSubscriptionComplete: () -> Unit,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        verticalAlignment = Alignment.Top,
        state = pagerState,
        modifier = modifier,
    ) { index ->
        when (index) {
            EditSubscriptionTab.NORMAL.ordinal -> {
                NormalCategoryPage(
                    categories = categories,
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            EditSubscriptionTab.DEPARTMENT.ordinal -> {
                DepartmentCategoryPage(
                    departments = departments,
                    onDepartmentClick = onDepartmentClick,
                    onAddDepartmentButtonClick = onAddDepartmentButtonClick,
                    onCallToActionClick = onSubscriptionComplete,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun NormalCategoryPage(
    categories: List<NormalSubscriptionUiModel>,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(top = 46.dp, start = 31.dp, end = 31.dp, bottom = 10.dp),
        modifier = modifier,
    ) {
        itemsIndexed(
            items = categories,
            key = { _, category -> category.categoryName },
        ) { index, category ->
            NormalSubscriptionItem(
                uiModel = category,
                onClick = { onCategoryClick(index) },
                modifier = Modifier.aspectRatio(1f, matchHeightConstraintsFirst = true),
            )
        }
    }
}

@Composable
private fun DepartmentCategoryPage(
    departments: List<DepartmentSubscriptionUiModel>,
    onDepartmentClick: (String) -> Unit,
    onAddDepartmentButtonClick: () -> Unit,
    onCallToActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (departments.isEmpty()) {
        DepartmentCategoryEmptyIndicator(
            onAddDepartmentButtonClick = onAddDepartmentButtonClick,
            modifier = modifier,
        )
    } else {
        DepartmentCategoryList(
            departments = departments,
            onDepartmentClick = onDepartmentClick,
            onCallToActionClick = onCallToActionClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun DepartmentCategoryEmptyIndicator(
    onAddDepartmentButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        DepartmentEmptyIndicatorText(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        KuringCallToAction(
            text = stringResource(id = R.string.department_subscription_add_department),
            onClick = onAddDepartmentButtonClick,
        )
    }
}

@Composable
private fun DepartmentEmptyIndicatorText(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.department_subscription_empty_message),
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 22.5.sp,
                fontFamily = Pretendard,
                fontWeight = FontWeight(500),
                color = KuringTheme.colors.textCaption2,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun DepartmentCategoryList(
    departments: List<DepartmentSubscriptionUiModel>,
    onDepartmentClick: (String) -> Unit,
    onCallToActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(
                items = departments,
                key = { it.name },
            ) { department ->
                DepartmentSubscriptionItem(
                    uiModel = department,
                    onClick = { onDepartmentClick(department.name) },
                )
            }
        }
        KuringCallToAction(
            text = stringResource(id = R.string.department_subscription_complete),
            onClick = onCallToActionClick,
            blur = true,
            modifier = Modifier
                .background(KuringTheme.colors.background)
                .fillMaxWidth(),
        )
    }
}

@LightAndDarkPreview
@Composable
private fun SubscriptionsPreview() {
    val categories =
        NormalSubscriptionUiModel.initialValues.mapIndexed { index, normalSubscriptionUiModel ->
            normalSubscriptionUiModel.copy(isSelected = index < 3)
        }
    val departments = List(50) {
        DepartmentSubscriptionUiModel("쿠링학과$it", it % 2 == 0)
    }
    var selectedTab by remember { mutableStateOf(EditSubscriptionTab.NORMAL) }

    KuringTheme {
        EditSubscriptionScreen(
            categories = categories,
            departments = departments,
            onNavigateToBack = {},
            onCategoryClick = {},
            onDepartmentClick = {},
            onAddDepartmentButtonClick = {},
            onSubscriptionComplete = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@LightPreview
@Composable
private fun DepartmentPagePreview_Empty() {
    KuringTheme {
        EditSubscriptionScreen(
            categories = emptyList(),
            departments = emptyList(),
            onNavigateToBack = {},
            onCategoryClick = {},
            onDepartmentClick = {},
            onAddDepartmentButtonClick = {},
            onSubscriptionComplete = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
