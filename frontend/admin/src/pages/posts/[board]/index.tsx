import { CustomButtons, IButtonProps } from '@components/Buttons'
import CustomAlert from '@components/CustomAlert'
import Search, { IKeywordType } from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
import { Box } from '@material-ui/core'
import Link from '@material-ui/core/Link'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import {
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
import FiberNewIcon from '@material-ui/icons/FiberNew'
import { ClassNameMap } from '@material-ui/styles'
// api
import {
  BoardSavePayload,
  boardService,
  CommentDeletePayload,
  postsService,
} from '@service'
import {
  conditionAtom,
  detailButtonsSnackAtom,
  errorStateSelector,
} from '@stores'
import { format, Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import classNames from 'classnames'
import { GetServerSideProps } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo, useRef, useState } from 'react'
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
    vMiddle: {
      verticalAlign: 'middle',
    },
    mgl: {
      marginLeft: theme.spacing(0.5),
    },
    cancel: {
      textDecoration: 'line-through',
    },
  }),
)

// 그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  handleDetail: (postsNo: number) => void,
  gridApiRef: React.MutableRefObject<any>,
  t?: TFunction,
  classes?: ClassNameMap<string>,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  handleDetail,
  gridApiRef,
  t,
  classes,
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
    field: 'postsTitle',
    headerName: t('posts.posts_title'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
    renderCell: function renderCellPostsTitle(params: GridValueGetterParams) {
      // eslint-disable-next-line no-param-reassign
      gridApiRef.current = params.api // api
      return (
        <Link
          href="#"
          onClick={(event: React.MouseEvent<HTMLAnchorElement>) => {
            event.preventDefault()
            handleDetail(params.row.postsNo)
          }}
        >
          <Box
            color="text.primary"
            component="span"
            className={classNames({
              [classes.cancel]: params.row.deleteAt,
            })}
          >
            {(params.row.noticeAt ? `[${t('common.notice')}] ` : '') +
              params.row.postsTitle}
            {params.row.commentCount && params.row.commentCount !== 0 ? (
              <Box
                color="red"
                component="span"
              >{` [${params.row.commentCount}]`}</Box>
            ) : (
              ''
            )}
            {params.row.isNew && (
              <FiberNewIcon
                color="secondary"
                className={classNames({
                  [classes.mgl]: true,
                  [classes.vMiddle]: true,
                })}
              />
            )}
          </Box>
        </Link>
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
      params.value
        ? convertStringToDateFormat(
            params.value as string,
            'yyyy-MM-dd HH:mm:ss',
          )
        : null,
  },
  {
    field: 'createdName',
    headerName: t('common.created_by'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
    sortable: false,
  },
  {
    field: 'readCount',
    headerName: t('common.read_count'),
    headerAlign: 'center',
    align: 'center',
    width: 120,
    sortable: false,
  },
  {
    field: 'deleteAt',
    headerName: t('label.button.delete'),
    headerAlign: 'center',
    align: 'center',
    width: 120,
    sortable: false,
    valueGetter: (params: GridValueGetterParams) => {
      if (params.value === 1) return '작성자'
      if (params.value === 2) return '관리자'
      return ''
    },
  },
]

const conditionKey = 'posts'

export interface IBoardProps {
  board: BoardSavePayload | null
}

// 실제 render되는 컴포넌트
const Posts = ({ board }: IBoardProps) => {
  // props 및 전역변수
  // const { id } = props
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()
  const gridApiRef = useRef<any>(null)

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 조회조건 select items
  const searchTypes: IKeywordType[] = [
    {
      key: 'postsData',
      label: `${t('posts.posts_title')}+${t('posts.posts_content')}`,
    },
    {
      key: 'postsName',
      label: t('posts.posts_title'),
    },
    {
      key: 'postsContent',
      label: t('posts.posts_content'),
    },
  ]

  /**
   * 상태관리 필요한 훅
   */
  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const [boardNo] = useState<number>(Number(route.query.board) || null)

  // 현 페이지내 필요한 hook
  const [page, setPage] = useState<number>(
    parseInt(route.query.page as string, 10) || 0,
  )
  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
  })

  // 목록 데이터 조회 및 관리
  const { data, mutate } = postsService.search(boardNo, {
    keywordType: keywordState?.keywordType || 'postsName',
    keyword: keywordState?.keyword || '',
    size: board.postDisplayCount,
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

  // 목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPage(0)
    }
  }

  // 상세 화면 이동
  const handleDetail = useCallback(
    (postsNo: number) => {
      route.push({
        pathname: `/posts/${boardNo}/view/${postsNo}`,
        /* query: {
          size: board.postDisplayCount,
          page,
          keywordType: keywordState?.keywordType,
          keyword: keywordState?.keyword,
        }, */
      })
    },
    [boardNo, route],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(
    () => getColumns(data, handleDetail, gridApiRef, t, classes),
    [data, handleDetail, t, classes, gridApiRef],
  )

  // datagrid page change event
  const handlePageChange = (_page: number, details?: any) => {
    setPage(_page)
  }

  // 그리드 체크 해제
  const uncheckedGrid = useCallback(() => {
    const selectedRowKeys = gridApiRef.current?.getSelectedRows().keys()

    let cnt = 0
    while (cnt < data.numberOfElements) {
      const gridRowId = selectedRowKeys.next()
      if (gridRowId.done === true) break
      gridApiRef.current.selectRow(gridRowId.value, false, false)
      cnt += 1
    }
  }, [data?.numberOfElements])

  // 선택된 행 수 반환
  const getSelectedRowCount = (deleteAt: boolean) => {
    let count = 0

    const selectedRows = gridApiRef.current.getSelectedRows()
    selectedRows.forEach(m => {
      if (deleteAt === null || deleteAt ? m.deleteAt !== 0 : m.deleteAt === 0) {
        count += 1
      }
    })

    return count
  }

  // 선택된 행 반환
  const getSelectedRows = (deleteAt: boolean) => {
    let list: CommentDeletePayload[] = []

    const selectedRows = gridApiRef.current.getSelectedRows()
    selectedRows.forEach(m => {
      if (
        deleteAt === null ||
        (deleteAt ? m.deleteAt !== 0 : m.deleteAt === 0)
      ) {
        const saved: CommentDeletePayload = {
          boardNo: m.boardNo,
          postsNo: m.postsNo,
        }
        list.push(saved)
      }
    })

    return list
  }

  // 성공 callback
  const successCallback = useCallback(() => {
    setSuccessSnackBar('success')

    uncheckedGrid()

    mutate()
  }, [mutate, setSuccessSnackBar, uncheckedGrid])

  // 삭제
  const handleRemove = useCallback(() => {
    const selectedRows = getSelectedRows(false)

    if (selectedRows.length === 0) {
      successCallback()
      return
    }

    postsService.remove({
      callback: successCallback,
      errorCallback,
      data: selectedRows,
    })
  }, [errorCallback, successCallback])

  // 복원
  const handleRestore = useCallback(() => {
    setSuccessSnackBar('loading')

    const selectedRows = getSelectedRows(true)

    if (selectedRows.length === 0) {
      successCallback()
      return
    }

    postsService.restore({
      callback: successCallback,
      errorCallback,
      data: selectedRows,
    })
  }, [setSuccessSnackBar, errorCallback, successCallback])

  // 완전 삭제
  const handleDelete = useCallback(() => {
    setSuccessSnackBar('loading')

    const selectedRows = getSelectedRows(null)

    if (selectedRows.length === 0) {
      successCallback()
      return
    }

    postsService.delete({
      callback: successCallback,
      errorCallback,
      data: selectedRows,
    })
  }, [setSuccessSnackBar, errorCallback, successCallback])

  // 삭제 버튼
  const removeButton: IButtonProps = {
    label: t('label.button.selection_delete'),
    variant: 'outlined',
    color: 'secondary',
    size: 'small',
    confirmMessage: t('msg.confirm.delete'),
    handleButton: handleRemove,
    validate: () => {
      if (gridApiRef.current.getSelectedRows().size === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.format'), [
            `${t('label.button.delete')} ${t('common.target')}`,
          ]),
        })
        return false
      }
      const count = getSelectedRowCount(false) // 미삭제만
      if (count === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.already_deleted.format'), [
            t('authorization'),
          ]),
        })
        return false
      }
      return true
    },
    completeMessage: t('msg.success.delete'),
  }

  // 복원 버튼
  const restoreButton: IButtonProps = {
    label: t('label.button.selection_restore'),
    variant: 'outlined',
    color: 'primary',
    size: 'small',
    confirmMessage: t('msg.confirm.restore'),
    handleButton: handleRestore,
    validate: () => {
      if (gridApiRef.current.getSelectedRows().size === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.format'), [
            `${t('label.button.restore')} ${t('common.target')}`,
          ]),
        })
        return false
      }
      const count = getSelectedRowCount(true) // 삭제만
      if (count === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.already_restored.format'), [
            t('authorization'),
          ]),
        })
        return false
      }
      return true
    },
    completeMessage: t('msg.success.restore'),
  }

  // 완전 삭제 버튼
  const deleteButton: IButtonProps = {
    label: t('label.button.selection_permanent_delete'),
    variant: 'outlined',
    color: 'secondary',
    size: 'small',
    confirmMessage: t('msg.confirm.permanent_delete'),
    handleButton: handleDelete,
    validate: () => {
      if (gridApiRef.current.getSelectedRows().size === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.format'), [
            `${t('label.button.permanent_delete')} ${t('common.target')}`,
          ]),
        })
        return false
      }
      return true
    },
    completeMessage: t('msg.success.permanent_delete'),
  }

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={() => {
          route.push(`${boardNo}/edit/-1`)
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
        pageSize={board.postDisplayCount}
        onPageChange={handlePageChange}
        getRowId={r => r.postsNo}
        checkboxSelection
        disableSelectionOnClick
      />
      <CustomButtons
        buttons={[removeButton, restoreButton, deleteButton]}
        className="containerLeft"
      />
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={() => setCustomAlert({ open: false })}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const boardNo = Number(query.board)

  let data = {}

  try {
    if (boardNo !== -1) {
      const result = await boardService.get(boardNo)
      if (result) {
        data = (await result.data) as BoardSavePayload
      }
    }
  } catch (error) {
    console.error(`board item query error ${error.message}`)
  }

  return {
    props: {
      board: data,
    },
  }
}

export default Posts
