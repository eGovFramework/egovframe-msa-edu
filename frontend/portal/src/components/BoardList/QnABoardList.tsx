import CollapsibleTable from '@components/TableList/CollapsibleTable'
import { convertStringToDateFormat, format as dateFormat } from '@libs/date'
import {
  GridCellParams,
  GridRowData,
  GridValueFormatterParams,
} from '@material-ui/data-grid'
import { Page } from '@service'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo } from 'react'
import { ColumnsType } from '.'

interface QnABaordListProps {
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
      width: 500,
      sortable: false,
      cellClassName: 'title',
    },
    {
      field: 'postsState',
      headerName: t('posts.posts_title'),
      headerAlign: 'center',
      width: 100,
      sortable: false,
      cellClassName: 'span',
      renderCell: (params: GridCellParams) => {
        /**
         * @todo
         * 상태 컬럼 생기면 수정 필요
         */
        if (params.value === 'ing') {
          return <span className="answering">{params.value}</span>
        } else {
          return <span>{params.value}test</span>
        }
      },
    },
    {
      field: 'createdDate',
      headerName: t('common.created_date'),
      headerAlign: 'center',
      align: 'center',
      width: 140,
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
      width: 100,
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
      renderCell: (params: GridCellParams) => {
        return (
          <div>
            <div className="title">{params.value}</div>
            <div className="sub">
              <p>{params.row.postsState}</p>
              <p>
                {convertStringToDateFormat(
                  params.row.createdDate,
                  'yyyy-MM-dd',
                )}
              </p>
              <p>{params.row.readCount}</p>
            </div>
          </div>
        )
      },
    },
  ]
}

const QnABaordList = ({ data, page, handleChangePage }: QnABaordListProps) => {
  const router = useRouter()
  const { t, i18n } = useTranslation()

  const columns = useMemo(() => getColumns(data, t), [data, router.query, i18n])
  const xsColumns = useMemo(
    () => getXsColumns(data, t),
    [data, router.query, i18n],
  )
  const renderCollapseRow = useCallback(
    (row: GridRowData) => {
      return (
        <>
          <p>{row['postContent']}</p>
          <p className="answer">{row['postAnswerContent']}</p>
        </>
      )
    },
    [data],
  )

  return (
    <div className="list">
      <CollapsibleTable
        columns={columns}
        xsColumns={xsColumns}
        hideColumns={true}
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

export { QnABaordList }
