import React, { useCallback, useMemo, useState } from 'react'
import { useRouter } from 'next/router'
import { NextPage } from 'next'
import { TFunction, useTranslation } from 'next-i18next'

// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
import Box from '@material-ui/core/Box'
import { Button } from '@material-ui/core'

// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import Search, { IKeywordType } from '@components/Search'
import { Page, rownum } from '@utils'

// 상태관리 recoil
import { useRecoilValue } from 'recoil'
import { conditionAtom } from '@stores'

// api
import { roleService } from '@service'
import usePage from '@hooks/usePage'
import { GRID_PAGE_SIZE } from '@constants'

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
    },
    iconButton: {
      padding: theme.spacing(1),
      marginLeft: theme.spacing(1),
      backgroundColor: theme.palette.background.default,
    },
    fab: {
      marginLeft: theme.spacing(1),
    },
  }),
)

// 그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  handleManageRole: (roleId: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (data, handleManageRole, t) => [
  {
    field: 'rownum',
    headerName: t('common.no'),
    headerAlign: 'center',
    align: 'center',
    sortable: false,
    valueGetter: (params: GridValueGetterParams) =>
      rownum(data, params.api.getRowIndex(params.id), 'asc'),
  },
  {
    field: 'roleId',
    headerName: t('role.role_id'),
    headerAlign: 'center',
    align: 'left',
    width: 200,
    sortable: false,
  },
  {
    field: 'roleName',
    headerName: t('role.role_name'),
    headerAlign: 'center',
    align: 'center',
    width: 200,
    sortable: false,
  },
  {
    field: 'roleContent',
    headerName: t('role.role_content'),
    headerAlign: 'center',
    flex: 1,
    sortable: false,
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
        <div>
          <Box>
            <Button
              variant="outlined"
              color="primary"
              size="small"
              /* eslint-disable-next-line @typescript-eslint/no-empty-function */
              onClick={() => {
                handleManageRole(params.row.roleId)
              }}
            >
              {t('role.manage_authorization')}
            </Button>
          </Box>
        </div>
      )
    },
  },
]

const conditionKey = 'role'

// 실제 render 컴포넌트
const Role: NextPage<any> = () => {
  // props 및 전역변수
  // const { id } = props
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 조회조건 select items
  const searchTypes: IKeywordType[] = [
    {
      key: 'roleName',
      label: t('role.role_name'),
    },
    {
      key: 'roleContent',
      label: t('role.role_content'),
    },
  ]

  /**
   * 상태관리 필요한 훅
   */

  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))

  // 현 페이지내 필요한 hook
  const { page, setPageValue } = usePage(conditionKey)

  // 목록 데이터 조회 및 관리
  const { data, mutate } = roleService.search({
    keywordType: keywordState?.keywordType || 'roleName',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
  })

  /**
   * 비지니스 로직
   */

  // 권한 인가 매핑 관리 화면 이동
  const handleManageRole = useCallback(
    (roleId: string) => {
      route.push(
        {
          pathname: `/role-authorization`,
          query: { roleId },
        },
        '/role-authorization',
      )
    },
    [route],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () => getColumns(data, handleManageRole, t),
    [data, handleManageRole, t],
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
        getRowId={r => r.roleId}
      />
    </div>
  )
}

export default Role
