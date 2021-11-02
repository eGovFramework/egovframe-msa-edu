import { GridButtons } from '@components/Buttons'
import Search from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
import { convertStringToDateFormat } from '@libs/date'
import Link from '@material-ui/core/Link'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import Typography from '@material-ui/core/Typography'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
import { attachmentService, fileService } from '@service'
import { conditionAtom, errorStateSelector } from '@stores'
import { formatBytes, Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import { TFunction } from 'next-i18next'
import React, { useCallback, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
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

type ColumnType = (
  data: Page,
  handleDelete: (id: string) => void,
  toggoleIsDelete: (
    event: React.ChangeEvent<HTMLInputElement>,
    id: string,
  ) => void,
  t: TFunction,
) => GridColDef[]

//그리드 컬럼 정의
const getColumns: ColumnType = (
  data: Page,
  handleDelete: (id: string) => void,
  toggoleIsDelete: (
    event: React.ChangeEvent<HTMLInputElement>,
    id: string,
  ) => void,
  t,
) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'),
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      valueGetter: (params: GridValueGetterParams) => {
        return rownum(
          data,
          data?.content.findIndex(v => v.id === params.id),
          'desc',
        )
      },
    },
    {
      field: 'code',
      headerName: t('attachment.file_id'),
      headerAlign: 'center',
      align: 'center',
      width: 150,
      sortable: false,
    },
    {
      field: 'seq',
      headerName: t('attachment.file_no'),
      headerAlign: 'center',
      align: 'center',
      width: 100,
      sortable: false,
    },
    {
      field: 'originalFileName',
      headerName: t('attachment.file_name'),
      headerAlign: 'center',
      align: 'left',
      width: 150,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <Typography>
          <Link
            href={`${fileService.downloadUrl}/${params.row.id}`}
            download={params.value}
            variant="body2"
            onClick={(event) => {
              event.preventDefault()
              attachmentService.download(params.row.id)
            }}
          >
            {params.value}
          </Link>
        </Typography>
      ),
    },
    {
      field: 'size',
      headerName: t('attachment.file_size'),
      headerAlign: 'center',
      align: 'right',
      width: 100,
      sortable: false,
      valueFormatter: (params: GridValueFormatterParams) => {
        return formatBytes(params.value as number)
      },
    },
    {
      field: 'downloadCnt',
      headerName: t('attachment.download_count'),
      headerAlign: 'center',
      align: 'right',
      width: 100,
      sortable: false,
    },
    {
      field: 'createDate',
      headerName: t('common.created_datetime'),
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
      valueFormatter: (params: GridValueFormatterParams) => {
        return convertStringToDateFormat(
          params.value as string,
          'yyyy-MM-dd HH:mm',
        )
      },
    },
    {
      field: 'isDelete',
      headerName: t('common.delete_at'),
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggoleIsDelete(event, params.row.id)
          }
        />
      ),
    },
    {
      field: 'id',
      headerName: t('common.manage'),
      headerAlign: 'center',
      align: 'center',
      width: 200,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <GridButtons
          id={params.value as string}
          handleDelete={(id: string) => handleDelete(id)}
        />
      ),
    },
  ]
}

const conditionKey = 'attachment'

const Attachment = () => {
  const classes = useStyles()
  const { t } = useTranslation()

  //조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  // 에러 상태관리
  const setErrorState = useSetRecoilState(errorStateSelector)
  // pagination 상태관리
  const { page, setPageValue } = usePage(conditionKey)
  //조회조건 select items
  const searchTypes = useSearchTypes([
    {
      key: 'id',
      label: t('attachment.file_id'),
    },
    {
      key: 'name',
      label: t('attachment.file_name'),
    },
  ])
  //목록 데이터 조회 및 관리
  const { data, mutate } = attachmentService.search({
    keywordType: keywordState?.keywordType || 'id',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
  })

  //에러 callback
  const errorCallback = useCallback((error: AxiosError) => {
    setErrorState({
      error,
    })
  }, [])

  //삭제여부 toggle 시 바로 update
  const toggoleIsDelete = useCallback(
    async (event: React.ChangeEvent<HTMLInputElement>, id: string) => {
      attachmentService.updateToggle({
        callback: mutate,
        errorCallback,
        id,
        isDelete: event.target.checked,
      })
    },
    [page],
  )

  const handleDelete = useCallback((id: string) => {
    attachmentService.delete({
      id,
      callback: mutate,
      errorCallback,
    })
  }, [])

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => {
    return getColumns(data, handleDelete, toggoleIsDelete, t)
  }, [data])

  //목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPageValue(0)
    }
  }

  //datagrid page change event
  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        conditionKey={conditionKey}
      />
      <CustomDataGrid
        classes={classes}
        rows={data?.content}
        columns={columns}
        rowCount={data?.totalElements}
        paginationMode="server"
        pageSize={GRID_PAGE_SIZE}
        page={page}
        onPageChange={handlePageChange}
      />
    </div>
  )
}

export default Attachment
