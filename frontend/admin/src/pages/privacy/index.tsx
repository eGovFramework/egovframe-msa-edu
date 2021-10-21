import { GridButtons } from '@components/Buttons'
import Search, { IKeywordType } from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
// api
import { privacyService } from '@service'
import {
  conditionAtom,
  detailButtonsSnackAtom,
  errorStateSelector,
} from '@stores'
import { Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import { NextPage } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo } from 'react'
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
  }),
)

// 그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  toggleUseAt,
  deletePrivacy: (privacyNo: string) => void,
  updatePrivacy: (privacyNo: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  toggleUseAt,
  deletePrivacy,
  updatePrivacy,
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
    field: 'privacyTitle',
    headerName: t('privacy.privacy_title'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
  },
  {
    field: 'useAt',
    headerName: t('common.use_at'),
    headerAlign: 'center',
    align: 'left',
    width: 150,
    sortable: false,
    renderCell: function renderCellCreatedAt(params: GridCellParams) {
      return (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleUseAt(event, params.row.privacyNo as number)
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
          id={params.row.privacyNo as string}
          handleDelete={deletePrivacy}
          handleUpdate={updatePrivacy}
        />
      )
    },
  },
]

const conditionKey = 'privacy'

// 실제 render되는 컴포넌트
const Privacy: NextPage<any> = () => {
  // props 및 전역변수
  // const { id } = props
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 조회조건 select items
  const searchTypes: IKeywordType[] = [
    {
      key: 'privacyTitle',
      label: t('privacy.privacy_title'),
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

  // 목록 데이터 조회 및 관리
  const { data, mutate } = privacyService.search({
    keywordType: keywordState?.keywordType || 'privacyTitle',
    keyword: keywordState?.keyword || '',
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
      paramPrivacyNo: string,
    ) => {
      setSuccessSnackBar('loading')

      await privacyService.updateUseAt({
        callback: successCallback,
        errorCallback,
        privacyNo: paramPrivacyNo,
        useAt: event.target.checked,
      })
    },
    [errorCallback, mutate],
  )

  // 삭제
  const deletePrivacy = useCallback(
    (privacyNo: string) => {
      setSuccessSnackBar('loading')

      privacyService.delete({
        privacyNo,
        callback: successCallback,
        errorCallback,
      })
    },
    [errorCallback, mutate],
  )

  // 수정 시 상세 화면 이동
  const updatePrivacy = useCallback(
    (privacyNo: string) => {
      route.push(`/privacy/${privacyNo}`)
    },
    [route],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () => getColumns(data, toggleUseAt, deletePrivacy, updatePrivacy, t),
    [data, toggleUseAt, deletePrivacy, updatePrivacy, t],
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

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={() => {
          route.push('privacy/-1')
        }}
        conditionKey={conditionKey}
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
        getRowId={r => r.privacyNo}
      />
    </div>
  )
}

export default Privacy
