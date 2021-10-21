import AttachList from '@components/AttachList'
import { CustomButtons, IButtonProps } from '@components/Buttons'
import { Comment } from '@components/comment'
import { convertStringToDateFormat } from '@libs/date'
import Box from '@material-ui/core/Box'
import Grid from '@material-ui/core/Grid'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import CommentIcon from '@material-ui/icons/Comment'
import {
  BoardSavePayload,
  boardService,
  fileService,
  IAttachmentResponse,
  IBoardProps,
  PostsSavePayload,
  postsService,
  SKINT_TYPE_CODE_FAQ,
  SKINT_TYPE_CODE_NORMAL,
  SKINT_TYPE_CODE_QNA,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { AxiosError } from 'axios'
import classNames from 'classnames'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useEffect, useState } from 'react'
import { useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginTop: theme.spacing(1),
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(2),
      },
    },
    content: {
      position: 'relative',
      padding: theme.spacing(2),
      minHeight: '120px',
    },
    contentTitle: {
      marginTop: theme.spacing(0),
    },
    contentCreator: {
      marginTop: theme.spacing(1),
      display: 'flex',
    },
    contentCreatorLeft: {
      flex: 1,
    },
    commentIcon: {
      marginRight: theme.spacing(0.5),
      verticalAlign: 'middle',
    },
    contentLabel: {
      display: 'block',
      position: 'absolute',
      left: '30px',
      top: '40px',
      width: '40px',
      height: '40px',
      fontSize: '20px',
      fontWeight: 700,
      textAlign: 'center',
      lineHeight: '40px',
      borderRadius: '50%',
      color: '#fff',
      backgroundColor: '#1a4890',
    },
    contentLabelQ: {
      backgroundColor: '#1a4890',
    },
    contentLabelA: {
      backgroundColor: '#5aab34',
    },
    contentEditor: {
      padding: theme.spacing(2, 2, 2, 10),
    },
    label: {
      padding: theme.spacing(2),
      textAlign: 'center',
      backgroundColor: theme.palette.background.default,
    },
    number: {
      padding: theme.spacing(2),
      textAlign: 'right',
    },
    mgt1: {
      marginTop: theme.spacing(1),
    },
    mgl3: {
      marginLeft: theme.spacing(3),
    },
  }),
)

export interface IPostsItemsProps {
  boardNo: number
  postsNo: number
  board: BoardSavePayload | null
  initData: PostsSavePayload | null
}

const PostsItem = ({ boardNo, postsNo, board, initData }: IPostsItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  const [deleteAt, setDeleteAt] = useState<number>(initData.deleteAt)

  const [commentCount, setCommentCount] = useState<number>(0)
  const refreshCommentCount = count => {
    setCommentCount(count)
  }

  const [attachData, setAttachData] = useState<
    IAttachmentResponse[] | undefined
  >(undefined)

  const getAttachments = useCallback(
    async (code: string) => {
      try {
        const result = await fileService.getAttachmentList(code)

        if (result) {
          setAttachData(result.data)
        }
      } catch (error) {
        setErrorState({
          error,
        })
      }
    },
    [setErrorState],
  )

  useEffect(() => {
    if (initData.attachmentCode) {
      getAttachments(initData.attachmentCode)
    }
  }, [getAttachments, initData.attachmentCode])

  // 목록 화면으로 이동
  const handleList = useCallback(() => {
    /* route.push(
      {
        pathname: `/posts/${boardNo}`,
        query: {
          size: route.query.size,
          page: route.query.page,
          keywordType: route.query.keywordType,
          keyword: route.query.keyword,
        },
      },
      // `/posts/${boardNo}`,
    ) */
    route.back()
  }, [route])

  // 수정 화면으로 이동
  const handleEdit = useCallback(() => {
    route.push({
      pathname: `/posts/${boardNo}/edit/${postsNo}`,
      query: {
        size: route.query.size,
        page: route.query.page,
        keywordType: route.query.keywordType,
        keyword: route.query.keyword,
      },
    })
  }, [boardNo, postsNo, route])

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
  const handleRemove = useCallback(() => {
    setSuccessSnackBar('loading')

    postsService.remove({
      callback: () => {
        setSuccessSnackBar('success')

        setDeleteAt(2) // 삭제 여부 - 1:작성자, 2:관리자
      },
      errorCallback,
      data: [
        {
          boardNo,
          postsNo,
        },
      ],
    })
  }, [setSuccessSnackBar, errorCallback, boardNo, postsNo])

  // 완전 삭제
  const handleDelete = useCallback(() => {
    setSuccessSnackBar('loading')

    postsService.delete({
      callback: () => {
        setSuccessSnackBar('success')

        handleList() // 목록 화면으로 이동
      },
      errorCallback,
      data: [
        {
          boardNo,
          postsNo,
        },
      ],
    })
  }, [setSuccessSnackBar, errorCallback, boardNo, postsNo, handleList])

  // 복원
  const handleRestore = useCallback(() => {
    setSuccessSnackBar('loading')

    postsService.restore({
      callback: () => {
        setSuccessSnackBar('success')

        setDeleteAt(0)
      },
      errorCallback,
      data: [
        {
          boardNo,
          postsNo,
        },
      ],
    })
  }, [setSuccessSnackBar, errorCallback, boardNo, postsNo])

  // 삭제 버튼
  const removeButton: IButtonProps = {
    label: t('label.button.delete'),
    variant: 'outlined',
    color: 'secondary',
    size: 'small',
    confirmMessage: t('msg.confirm.delete'),
    handleButton: handleRemove,
  }

  // 복원 버튼
  const restoreButton: IButtonProps = {
    label: t('label.button.restore'),
    variant: 'outlined',
    color: 'primary',
    size: 'small',
    confirmMessage: t('msg.confirm.restore'),
    handleButton: handleRestore,
  }

  // 완전 삭제 버튼
  const deleteButton: IButtonProps = {
    label: t('label.button.permanent_delete'),
    variant: 'outlined',
    color: 'secondary',
    size: 'small',
    confirmMessage: t('msg.confirm.permanent_delete'),
    handleButton: handleDelete,
  }

  // 수정 버튼
  const editButton: IButtonProps = {
    label: t('label.button.edit'),
    variant: 'outlined',
    color: 'primary',
    size: 'small',
    handleButton: handleEdit,
  }

  // 목록 버튼
  const listButton: IButtonProps = {
    label: t('label.button.list'),
    variant: 'outlined',
    size: 'small',
    handleButton: handleList,
  }

  // 하단 버튼
  let leftButtons = []

  // 삭제/복원 버튼 추가
  if (deleteAt === 0) {
    leftButtons.push(removeButton)
  } else {
    leftButtons.push(restoreButton)
  }
  leftButtons.push(deleteButton)

  return (
    <div className={classes.root}>
      <Grid container spacing={1}>
        <Grid item xs={12} sm={12}>
          <Box boxShadow={1} className={classes.content}>
            <Typography variant="h3">
              {(initData.noticeAt ? '[공지] ' : '') + initData.postsTitle}
            </Typography>
            <Box className={classes.contentCreator}>
              <Box className={classes.contentCreatorLeft}>
                <Typography variant="h6" component="h4">
                  {initData.createdName}
                </Typography>
                <Box component="span">
                  {convertStringToDateFormat(
                    initData.createdDate,
                    'yyyy-MM-dd HH:mm:ss',
                  )}
                </Box>
                <Box component="span" className={classes.mgl3}>
                  {`${t('common.read')} ${initData.readCount}`}
                </Box>
              </Box>
              {board?.commentUseAt && (
                <Box>
                  <CommentIcon
                    fontSize="small"
                    className={classes.commentIcon}
                  />
                  {`${t('comment')} ${commentCount}`}
                </Box>
              )}
            </Box>
          </Box>
        </Grid>
        <Grid item xs={12} sm={12}>
          {board.uploadUseAt && attachData && (
            <AttachList data={attachData} setData={setAttachData} readonly />
          )}
        </Grid>
        <Grid item xs={12} sm={12}>
          {(board.skinTypeCode === SKINT_TYPE_CODE_FAQ ||
            board.skinTypeCode === SKINT_TYPE_CODE_QNA) && (
            <Box boxShadow={1} className={classes.content}>
              <div
                className={classNames({
                  [classes.contentLabel]: true,
                  [classes.contentLabelQ]: true,
                })}
              >
                Q
              </div>
              <div
                className={classes.contentEditor}
                dangerouslySetInnerHTML={{ __html: initData.postsContent }}
              />
            </Box>
          )}
          {board.skinTypeCode === SKINT_TYPE_CODE_NORMAL && (
            <Box boxShadow={1} className={classes.content}>
              <div
                dangerouslySetInnerHTML={{ __html: initData.postsContent }}
              />
            </Box>
          )}
        </Grid>
        {(board.skinTypeCode === SKINT_TYPE_CODE_FAQ ||
          board.skinTypeCode === SKINT_TYPE_CODE_QNA) && (
          <Grid item xs={12} sm={12}>
            <Box boxShadow={1} className={classes.content}>
              <div
                className={classNames({
                  [classes.contentLabel]: true,
                  [classes.contentLabelA]: true,
                })}
              >
                A
              </div>
              <div
                className={classes.contentEditor}
                dangerouslySetInnerHTML={{
                  __html: initData.postsAnswerContent,
                }}
              />
            </Box>
          </Grid>
        )}
      </Grid>
      {board?.commentUseAt && (
        <Comment
          boardNo={boardNo}
          postsNo={postsNo}
          commentUseAt={board.commentUseAt}
          deleteAt={deleteAt}
          refreshCommentCount={refreshCommentCount}
        />
      )}
      <CustomButtons buttons={leftButtons} className="containerLeft" />
      <CustomButtons
        buttons={[editButton, listButton]}
        className="containerRight"
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const boardNo = Number(query.board)
  const postsNo = Number(query.id)

  let board: IBoardProps
  let data = {}

  try {
    if (postsNo !== -1) {
      const result = await postsService.get(boardNo, postsNo)
      if (result) {
        board = (await result.data?.board) as IBoardProps
        data = (await result.data) as PostsSavePayload
      }
    } else {
      const result = await boardService.get(boardNo)
      if (result) {
        board = (await result.data) as IBoardProps
      }
    }
  } catch (error) {
    console.error(`posts item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      boardNo,
      postsNo,
      board,
      initData: data,
    },
  }
}

export default PostsItem
