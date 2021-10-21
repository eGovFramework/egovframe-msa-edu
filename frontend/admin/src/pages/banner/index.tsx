import { GridButtons } from '@components/Buttons'
import Search, { IKeywordType } from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
import Box from '@material-ui/core/Box'
import MenuItem from '@material-ui/core/MenuItem'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
// api
import { bannerService, ISite } from '@service'
import {
  conditionAtom,
  conditionValue,
  detailButtonsSnackAtom,
  errorStateSelector,
} from '@stores'
import { Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps, NextPage } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo, useState } from 'react'
// 상태관리 recoil
import { useRecoilValue, useSetRecoilState } from 'recoil'

// material-ui style
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(1),
      },
    },
    search: {
      padding: theme.spacing(1),
      textAlign: 'center',
      width: '18vw',
      minWidth: 80,
      maxWidth: 200,
    },
  }),
)

// 그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  toggleUseAt,
  deleteBanner: (bannerNo: string) => void,
  updateBanner: (bannerNo: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  toggleUseAt,
  deleteBanner,
  updateBanner,
  t,
) => [
  {
    field: 'rownum',
    headerName: t('common.no'),
    headerAlign: 'center',
    align: 'center',
    width: 80,
    sortable: false,
    valueGetter: (params: GridValueGetterParams) =>
      rownum(data, params.api.getRowIndex(params.id), 'asc'),
  },
  {
    field: 'siteName',
    headerName: t('menu.site'),
    headerAlign: 'center',
    align: 'center',
    width: 80,
    sortable: false,
  },
  {
    field: 'bannerTypeCodeName',
    headerName: t('banner.banner_type_code'),
    headerAlign: 'center',
    align: 'center',
    width: 120,
    sortable: false,
  },
  {
    field: 'bannerTitle',
    headerName: t('banner.banner_title'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
  },
  {
    field: 'useAt',
    headerName: t('common.use_at'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
    sortable: false,
    renderCell: function renderCellCreatedAt(params: GridCellParams) {
      return (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleUseAt(event, params.row.bannerNo)
          }
        />
      )
    },
  },
  {
    field: 'createdDate',
    headerName: t('common.created_datetime'),
    headerAlign: 'center',
    align: 'center',
    width: 200,
    sortable: false,
    valueFormatter: (params: GridValueFormatterParams) =>
      convertStringToDateFormat(params.value as string, 'yyyy-MM-dd HH:mm:ss'),
  },
  {
    field: 'buttons',
    headerName: t('common.manage'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
    sortable: false,
    renderCell: function renderCellButtons(params: GridCellParams) {
      return (
        <GridButtons
          id={params.row.bannerNo as string}
          handleDelete={deleteBanner}
          handleUpdate={updateBanner}
        />
      )
    },
  },
]

const conditionKey = 'banner'

interface BannerProps {
  sites: ISite[]
}

// 실제 render되는 컴포넌트
const Banner: NextPage<BannerProps> = ({ sites }) => {
  // props 및 전역변수
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 조회조건 select items
  const searchTypes: IKeywordType[] = [
    {
      key: 'bannerTitle',
      label: t('banner.banner_title'),
    },
    {
      key: 'bannerContent',
      label: t('banner.banner_content'),
    },
  ]

  /**
   * 상태관리 필요한 훅
   */
  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const setErrorState = useSetRecoilState(errorStateSelector)

  // 현 페이지내 필요한 hook
  const { page, setPageValue } = usePage(conditionKey)
  const [customKeyword, setCustomKeyword] = useState<conditionValue>({
    siteId: keywordState?.siteId || '-',
  })

  // 목록 데이터 조회 및 관리
  const { data, mutate } = bannerService.search({
    keywordType: keywordState?.keywordType || 'bannerName',
    keyword: keywordState?.keyword || '',
    siteId: keywordState?.siteId === '-' ? '' : keywordState?.siteId,
    size: GRID_PAGE_SIZE,
    page,
  })

  /**
   * 비지니스 로직
   */

  // 에러 callback
  const errorCallback = useCallback(
    (error: AxiosError) => {
      setSuccessSnackBar('none')

      setErrorState({
        error,
      })
    },
    [setErrorState, setSuccessSnackBar],
  )

  // 성공 callback
  const successCallback = useCallback(() => {
    setSuccessSnackBar('success')

    mutate()
  }, [mutate, setSuccessSnackBar])

  // 사용 여부 toggle 시 save
  const toggleUseAt = useCallback(
    async (
      event: React.ChangeEvent<HTMLInputElement>,
      paramBannerNo: string,
    ) => {
      setSuccessSnackBar('loading')

      await bannerService.updateUseAt({
        callback: successCallback,
        errorCallback,
        bannerNo: paramBannerNo,
        useAt: event.target.checked,
      })
    },
    [errorCallback, setSuccessSnackBar, successCallback],
  )

  // 삭제
  const deleteBanner = useCallback(
    (bannerNo: string) => {
      setSuccessSnackBar('loading')

      bannerService.delete({
        bannerNo,
        callback: successCallback,
        errorCallback,
      })
    },
    [errorCallback, setSuccessSnackBar, successCallback],
  )

  // 수정 시 상세 화면 이동
  const updateBanner = useCallback(
    (bannerNo: string) => {
      route.push(`/banner/${bannerNo}`)
    },
    [route],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () => getColumns(data, toggleUseAt, deleteBanner, updateBanner, t),
    [data, toggleUseAt, deleteBanner, updateBanner, t],
  )

  // 목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPageValue(0)
    }
  }

  // datagrid page change event
  const handlePageChange = (_page: number, details?: any) => {
    setPageValue(_page)
  }

  // 조회조건 select onchange
  const handleSiteIdChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.preventDefault()
    setCustomKeyword({
      siteId: event.target.value,
    })
  }

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={() => {
          route.push('banner/-1')
        }}
        conditionKey={conditionKey}
        customKeyword={customKeyword}
        conditionNodes={
          <Box className={classes.search}>
            <TextField
              id="select-parentCodeId"
              select
              value={customKeyword?.siteId}
              onChange={handleSiteIdChange}
              variant="outlined"
              fullWidth
            >
              <MenuItem key="-" value="-">
                {t('common.all')}
              </MenuItem>
              {sites.map(option => (
                <MenuItem key={option.id} value={option.id}>
                  {option.name}
                </MenuItem>
              ))}
            </TextField>
          </Box>
        }
      />
      <CustomDataGrid
        page={page}
        classes={classes}
        rows={data?.content}
        columns={columns}
        rowCount={data?.totalElements}
        paginationMode="server"
        pageSize={GRID_PAGE_SIZE}
        onPageChange={handlePageChange}
        getRowId={r => r.bannerNo}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let sites: ISite[] = []

  try {
    const result = await bannerService.getSites()

    if (sites) {
      sites = result
    }
  } catch (error) {
    console.error(`banner site  getServerSideProps error ${error.message}`)
  }

  return {
    props: {
      sites,
    },
  }
}

export default Banner
