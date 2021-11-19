import { CustomButtons, IButtonProps } from '@components/Buttons'
import { ConfirmDialog } from '@components/Confirm'
import { PopupProps } from '@components/DialogPopup'
import Search, { IKeywordType } from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
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
import { boardService } from '@service'
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
  }),
)

// 그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  buttons: IButtonProps[],
  t?: TFunction,
  handlePopup?: (row: any) => void,
) => GridColDef[]

const getColumns: ColumnsType = (data, buttons, t, handlePopup) => [
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
    field: 'boardName',
    headerName: t('board.board_name'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
  },
  {
    field: 'skinTypeCodeName',
    headerName: t('board.skin_type_code'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
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
    width: 250,
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
        <CustomButtons buttons={buttons} row={params.row} />
      )
    },
  },
]

const conditionKey = 'board'
export type BoardProps = PopupProps

// 실제 render되는 컴포넌트
const Board: NextPage<BoardProps> = props => {
  // props 및 전역변수
  const { handlePopup } = props
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 조회조건 select items
  const searchTypes: IKeywordType[] = useSearchTypes([
    {
      key: 'boardName',
      label: t('board.board_name'),
    },
  ])

  /**
   * 상태관리 필요한 훅
   */
  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const setErrorState = useSetRecoilState(errorStateSelector)

  // 현 페이지내 필요한 hook
  const { page, setPageValue } = usePage(conditionKey)

  const [deleteConfirmState, setDeleteConfirmState] = useState<{
    open: boolean
    boardNo: number
  }>({
    open: false,
    boardNo: null,
  })

  // 목록 데이터 조회 및 관리
  const { data, mutate } = boardService.search({
    keywordType: keywordState?.keywordType || 'boardName',
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
  const handleDelete = useCallback(
    (row: any) => {
      const { boardNo, isPosts } = row

      if (isPosts) {
        setDeleteConfirmState({
          open: true,
          boardNo,
        })
        return
      }

      deleteBoard(boardNo)
    },
    [errorCallback, mutate, setSuccessSnackBar],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () =>
      getColumns(
        data,
        [
          {
            label: `${t('posts')} ${t('common.manage')}`,
            variant: 'outlined',
            size: 'small',
            handleButton: (row: any) => {
              route.push(`/posts/${row.boardNo}`)
            },
          },
          {
            label: t('label.button.edit'),
            variant: 'outlined',
            color: 'primary',
            size: 'small',
            handleButton: (row: any) => {
              route.push(`/board/${row.boardNo}`)
            },
          },
          {
            label: t('label.button.delete'),
            variant: 'outlined',
            color: 'secondary',
            size: 'small',
            confirmMessage: t('msg.confirm.delete'),
            handleButton: handleDelete,
            completeMessage: t('msg.success.delete'),
          },
        ],
        t,
        handlePopup,
      ),
    [data, t, handleDelete, handlePopup, route],
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

  const handleRegister = () => {
    route.push('board/-1')
  }

  const handleConfirmClose = () => {
    setDeleteConfirmState({
      open: false,
      boardNo: null,
    })
  }

  const deleteBoard = (boardNo: number) => {
    setSuccessSnackBar('loading')

    boardService.delete({
      boardNo,
      callback: () => {
        setSuccessSnackBar('success')

        mutate()
      },
      errorCallback,
    })
  }

  const handleConfirm = () => {
    const { boardNo } = deleteConfirmState
    deleteBoard(boardNo)
    setDeleteConfirmState({
      open: false,
      boardNo: null,
    })
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
        getRowId={r => r.boardNo}
      />
      <ConfirmDialog
        open={deleteConfirmState.open}
        contentText={'게시물이 존재합니다. 삭제하시겠습니까?'}
        handleClose={handleConfirmClose}
        handleConfirm={handleConfirm}
      />
    </div>
  )
}

export default Board
