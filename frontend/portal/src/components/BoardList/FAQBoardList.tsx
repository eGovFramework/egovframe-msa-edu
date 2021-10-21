import CollapsibleTable from '@components/TableList/CollapsibleTable'
import { convertStringToDateFormat, format as dateFormat } from '@libs/date'
import {
  GridCellParams,
  GridRowData,
  GridValueFormatterParams,
} from '@material-ui/data-grid'
import { Page } from '@service'
import { useTranslation } from 'next-i18next'
import React, { useCallback, useMemo } from 'react'
import { ColumnsType } from '.'

interface FAQBoardListProps {
  data: Page
  pageSize: number
  page: number
  handleChangePage: (
    event: React.MouseEvent<HTMLButtonElement> | null,
    page: number,
  ) => void
}

const getColumns: ColumnsType = (data, t) => {
  return [
    {
      field: 'postsTitle',
      headerName: t('posts.posts_title'),
      headerAlign: 'center',
      minWidth: 400,
      sortable: false,
      cellClassName: 'title',
    },
    {
      field: 'createdDate',
      headerName: t('common.created_date'),
      headerAlign: 'center',
      align: 'center',
      minWidth: 140,
      cellClassName: 'span',
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
      cellClassName: 'count',
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
          <p>
            {convertStringToDateFormat(params.row.createdDate, 'yyyy-MM-dd')}
          </p>
          <p>{params.row.readCount}</p>
        </div>
      </div>
    )
  }
}

const FAQBaordList = ({
  data,
  pageSize,
  page,
  handleChangePage,
}: FAQBoardListProps) => {
  const { t } = useTranslation()

  const columns = useMemo(() => getColumns(data, t), [data, t])
  const xsColumns = useMemo(() => getXsColumns(data, t), [data, t])

  const renderCollapseRow = useCallback((row: GridRowData) => {
    return (
      <>
        <p dangerouslySetInnerHTML={{ __html: row.postsContent }} />
        <p
          className="answer"
          dangerouslySetInnerHTML={{ __html: row.postsAnswerContent }}
        />
      </>
    )
  }, [])

  return (
    <div className="list">
      <CollapsibleTable
        columns={columns}
        xsColumns={xsColumns}
        hideColumns
        rowId="postsNo"
        rows={data?.content || []}
        renderCollapseRow={renderCollapseRow}
        page={page}
        first={data?.first}
        last={data?.last}
        totalPages={data?.totalPages}
        handleChangePage={handleChangePage}
      />
    </div>
  )
}

export { FAQBaordList }
