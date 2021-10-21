import { GridButtons } from '@components/Buttons'
import Search, { IKeywordType } from '@components/Search'
// 내부 컴포넌트 및 custom hook, etc...
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  GridCellParams,
  GridColDef,
  GridValueGetterParams,
} from '@material-ui/data-grid'
// api
import { authorizationService } from '@service'
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
  deleteAuthorization: (authorizationNo: string) => void,
  updateAuthorization: (authorizationNo: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  deleteAuthorization,
  updateAuthorization,
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
    field: 'authorizationName',
    headerName: t('authorization.authorization_name'),
    headerAlign: 'center',
    align: 'left',
    width: 250,
    sortable: false,
  },
  {
    field: 'urlPatternValue',
    headerName: t('authorization.url_pattern_value'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
  },
  {
    field: 'httpMethodCode',
    headerName: t('authorization.url_pattern_value'),
    headerAlign: 'center',
    align: 'center',
    width: 140,
    sortable: false,
  },
  {
    field: 'sortSeq',
    headerName: t('common.sort_seq'),
    headerAlign: 'center',
    align: 'center',
    width: 110,
    sortable: false,
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
          id={params.row.authorizationNo as string}
          handleDelete={deleteAuthorization}
          handleUpdate={updateAuthorization}
        />
      )
    },
  },
]

const conditionKey = 'authorization'

// 실제 render되는 컴포넌트
const Authorization: NextPage<any> = () => {
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
      key: 'authorizationName',
      label: t('authorization.authorization_name'),
    },
    {
      key: 'urlPatternValue',
      label: t('authorization.url_pattern_value'),
    },
    {
      key: 'httpMethodCode',
      label: t('authorization.http_method_code'),
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
  const { data, mutate } = authorizationService.search({
    keywordType: keywordState?.keywordType || 'authorizationName',
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

  // 삭제
  const deleteAuthorization = useCallback(
    (authorizationNo: string) => {
      setSuccessSnackBar('loading')

      authorizationService.delete({
        authorizationNo,
        callback: () => {
          setSuccessSnackBar('success')

          mutate()
        },
        errorCallback,
      })
    },
    [errorCallback, mutate, setSuccessSnackBar],
  )

  // 수정 시 상세 화면 이동
  const updateAuthorization = useCallback(
    (authorizationNo: string) => {
      route.push(`/authorization/${authorizationNo}`)
    },
    [route],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () => getColumns(data, deleteAuthorization, updateAuthorization, t),
    [data, deleteAuthorization, updateAuthorization, t],
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
          route.push('authorization/-1')
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
        getRowId={r => r.authorizationNo}
      />
    </div>
  )
}

export default Authorization
