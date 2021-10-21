import { SelectBox, SelectType } from '@components/Inputs'
import Search from '@components/Search'
import DataGridTable from '@components/TableList/DataGridTable'
import { GRID_ROWS_PER_PAGE_OPTION } from '@constants'
import useSearchTypes from '@hooks/useSearchTypes'
import { convertStringToDateFormat, format as dateFormat } from '@libs/date'
import { Box } from '@material-ui/core'
import {
  GridCellParams,
  GridValueFormatterParams,
  GridValueGetterParams,
  MuiEvent,
} from '@material-ui/data-grid'
import FiberNewIcon from '@material-ui/icons/FiberNew'
import { Page } from '@service'
import { conditionAtom } from '@stores'
import { rownum } from '@utils'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { createRef, useMemo } from 'react'
import { useRecoilValue } from 'recoil'
import { ColumnsType } from '.'

const getColumns: ColumnsType = (data, t) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'),
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      valueGetter: (params: GridValueGetterParams) =>
        rownum(data, params.api.getRowIndex(params.id), 'desc'),
    },
    {
      field: 'postsTitle',
      headerName: t('posts.posts_title'),
      headerAlign: 'center',
      flex: 1,
      sortable: false,
      cellClassName: 'title',
      renderCell: function renderCellPostsTitle(params: GridValueGetterParams) {
        // eslint-disable-next-line no-param-reassign
        // gridApiRef.current = params.api // api
        return (
          <>
            {params.row.noticeAt ? `[${t('common.notice')}] ` : ''}
            {params.row.postsTitle}
            {params.row.commentCount && params.row.commentCount !== 0 ? (
              <Box
                color="red"
                component="span"
              >{` [${params.row.commentCount}]`}</Box>
            ) : (
              ''
            )}
            {params.row.isNew && <FiberNewIcon color="secondary" />}
          </>
        )
      },
    },
    {
      field: 'createdName',
      headerName: t('common.created_by'),
      headerAlign: 'center',
      align: 'center',
      minWidth: 110,
      sortable: false,
    },
    {
      field: 'createdDate',
      headerName: t('common.created_date'),
      headerAlign: 'center',
      align: 'center',
      minWidth: 140,
      sortable: false,
      valueFormatter: (params: GridValueFormatterParams) =>
        params.value
          ? dateFormat(new Date(params.value as string), 'yyyy-MM-dd')
          : null,
    },
    {
      field: 'readCount',
      headerName: t('common.read_count'),
      headerAlign: 'center',
      align: 'center',
      minWidth: 100,
      sortable: false,
    },
  ]
}

const getXsColumns: ColumnsType = (data, t) => {
  return [
    {
      field: 'postsTitle',
      headerName: t('posts.posts_title'),
      headerAlign: 'center',
      sortable: false,
      renderCell,
    },
  ]

  function renderCell(params: GridCellParams) {
    return (
      <div>
        <div className="title">{params.value}</div>
        <div className="sub">
          <p>{params.row.createdName}</p>
          <p>
            {convertStringToDateFormat(params.row.createdDate, 'yyyy-MM-dd')}
          </p>
          <p>{params.row.readCount}</p>
        </div>
      </div>
    )
  }
}

interface NormalBoardListProps {
  data: Page
  conditionKey: string
  pageSize: number
  handlePageSize: (size: number) => void
  page: number
  handlePageChange: (page: number, details?: any) => void
  handleSearch: () => void
}

const NormalBoardList = (props: NormalBoardListProps) => {
  const {
    data,
    conditionKey,
    handleSearch,
    pageSize,
    page,
    handlePageSize,
    handlePageChange,
  } = props
  const { t } = useTranslation()
  const router = useRouter()

  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))

  const pageSizeRef = createRef<SelectType>()

  // 조회조건 select items
  const searchTypes = useSearchTypes([
    {
      value: 'postsTitle',
      label: t('posts.posts_title'),
    },
    {
      value: 'postsContent',
      label: t('posts.posts_content'),
    },
  ])

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    handlePageSize(parseInt(e.target.value, 10))
  }

  const handleCellClick = (
    params: GridCellParams,
    event: MuiEvent<React.MouseEvent>,
  ) => {
    if (params.field !== 'postsTitle') {
      return
    }
    router.push(
      `${router.asPath}/view/${
        params.id
      }?size=${pageSize}&page=${page}&keywordType=${
        typeof keywordState?.keywordType === 'undefined'
          ? ''
          : keywordState?.keywordType
      }&keyword=${
        typeof keywordState?.keyword === 'undefined'
          ? ''
          : keywordState?.keyword
      }`,
    )
  }

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => getColumns(data, t), [data, t])
  const xsColumns = useMemo(() => getXsColumns(data, t), [data, t])
  const rowsPerPageSizeOptinos = GRID_ROWS_PER_PAGE_OPTION.map(item => {
    return {
      value: item,
      label: `${item} 개`,
    }
  })

  return (
    <>
      <fieldset>
        <div>
          <SelectBox
            ref={pageSizeRef}
            options={rowsPerPageSizeOptinos}
            customHandleChange={handlePageSizeChange}
          />
        </div>
        <div>
          <Search
            options={searchTypes}
            conditionKey={conditionKey}
            handleSearch={handleSearch}
          />
        </div>
      </fieldset>
      <DataGridTable
        columns={columns}
        rows={data?.content}
        xsColumns={xsColumns}
        getRowId={r => r.postsNo}
        pageSize={pageSize}
        rowCount={data?.totalElements}
        page={page}
        onPageChange={handlePageChange}
        paginationMode="server"
        onCellClick={handleCellClick}
      />
    </>
  )
}
export { NormalBoardList }
