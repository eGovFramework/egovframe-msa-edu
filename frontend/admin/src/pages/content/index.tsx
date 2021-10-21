import { GridButtons } from '@components/Buttons'
import { PopupProps } from '@components/DialogPopup'
import Search, { IKeywordType } from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
import Button from '@material-ui/core/Button'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
// api
import { contentService } from '@service'
import { conditionAtom, errorStateSelector } from '@stores'
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
  deleteContent: (contentNo: string) => void,
  updateContent: (contentNo: string) => void,
  t?: TFunction,
  handlePopup?: (row: any) => void,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  deleteContent,
  updateContent,
  t,
  handlePopup,
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
    field: 'contentName',
    headerName: t('content.content_name'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
  },
  {
    field: 'lastModifiedBy',
    headerName: t('common.last_modified_by'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
    sortable: false,
  },
  {
    field: 'modifiedDate',
    headerName: t('common.modified_date'),
    headerAlign: 'center',
    align: 'center',
    width: 200,
    sortable: false,
    valueFormatter: (params: GridValueFormatterParams) =>
      convertStringToDateFormat(params.value as string, 'yyyy-MM-dd HH:mm:ss'),
  },
  {
    field: 'buttons',
    headerName: handlePopup ? t('common.select') : t('common.manage'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
    sortable: false,
    renderCell: function renderCellButtons(params: GridCellParams) {
      return handlePopup ? (
        <Button
          onClick={() => {
            handlePopup(params.row)
          }}
          variant="outlined"
          color="inherit"
          size="small"
        >
          {t('common.select')}
        </Button>
      ) : (
        <GridButtons
          id={params.row.contentNo as string}
          handleDelete={deleteContent}
          handleUpdate={updateContent}
        />
      )
    },
  },
]

const conditionKey = 'content'
export interface ContentProps extends PopupProps {}

// 실제 render되는 컴포넌트
const Content: NextPage<ContentProps> = props => {
  // props 및 전역변수
  const { handlePopup } = props
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 조회조건 select items
  const searchTypes: IKeywordType[] = [
    {
      key: 'contentName',
      label: t('content.content_name'),
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
  const { data, mutate } = contentService.search({
    keywordType: keywordState?.keywordType || 'contentName',
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
      setErrorState({
        error,
      })
    },
    [setErrorState],
  )

  // 삭제
  const deleteContent = useCallback(
    (contentNo: string) => {
      contentService.delete({
        contentNo,
        callback: mutate,
        errorCallback,
      })
    },
    [errorCallback, mutate],
  )

  // 수정 시 상세 화면 이동
  const updateContent = useCallback(
    (contentNo: string) => {
      route.push(`/content/${contentNo}`)
    },
    [route],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () => getColumns(data, deleteContent, updateContent, t, handlePopup),
    [data, deleteContent, updateContent, t, handlePopup],
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
  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  const handleRegister = () => {
    route.push('content/-1')
  }

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={handlePopup ? undefined : handleRegister}
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
        getRowId={r => r.contentNo}
      />
    </div>
  )
}

export default Content
