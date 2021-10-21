import React, { useCallback, useEffect, useState } from 'react'
import { AxiosError } from 'axios'
import { useSetRecoilState } from 'recoil'
import { useTranslation } from 'next-i18next'
import classNames from 'classnames'

import {
  createStyles,
  makeStyles,
  Theme,
  useTheme,
} from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import Card from '@material-ui/core/Card'
import CardActions from '@material-ui/core/CardActions'
import CardContent from '@material-ui/core/CardContent'
import Typography from '@material-ui/core/Typography'
import ErrorOutlineIcon from '@material-ui/icons/ErrorOutline'
import RefreshIcon from '@material-ui/icons/Refresh'
import Link from '@material-ui/core/Link'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import Button from '@material-ui/core/Button'

import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import useUser from '@hooks/useUser'
import { commentService, IComment } from '@service'
import { convertStringToDateFormat } from '@libs/date'
import { ConfirmDialog, ConfirmDialogProps } from '@components/Confirm'
import { CustomButtons } from '@components/Buttons'
import { CommentForm } from './form'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    commentRoot: {
      marginTop: theme.spacing(1),
      padding: theme.spacing(0, 2, 1, 2),
    },
    commentBox: {
      padding: theme.spacing(2, 2, 1, 2),
    },
    commentTitle: {
      display: 'flex',
      padding: theme.spacing(2, 1),
    },
    commentView: {
      padding: theme.spacing(0),
    },
    commentIcon: {
      marginRight: theme.spacing(0.5),
      verticalAlign: 'middle',
    },
    commentContent: {
      whiteSpace: 'pre-wrap',
    },
    commentDate: {
      marginRight: theme.spacing(3),
      padding: theme.spacing(1, 0),
    },
    moreBox: {
      textAlign: 'center',
      marginTop: theme.spacing(2),
    },
    black: {
      color: 'black',
    },
    ml1: {
      marginLeft: theme.spacing(1),
    },
    pd1: {
      padding: theme.spacing(1),
    },
    pdtb1: {
      padding: theme.spacing(1, 0),
    },
    cancel: {
      textDecoration: 'line-through',
    },
  }),
)

interface ICommentProps {
  boardNo: number
  postsNo: number
  commentUseAt: boolean
  deleteAt: number
  // eslint-disable-next-line @typescript-eslint/ban-types
  refreshCommentCount: (count) => void
}

interface ICommentSearchProps {
  _page: number
  _mode: 'replace' | 'append' | 'until'
}

const Comment: React.FC<ICommentProps> = ({
  boardNo,
  postsNo,
  commentUseAt,
  deleteAt,
  refreshCommentCount,
}: ICommentProps) => {
  const classes = useStyles()
  const { user } = useUser()
  const { t } = useTranslation()
  const theme = useTheme()
  const pagingSize = 2

  // 현 페이지내 필요한 hook
  const [page, setPage] = useState<number>(undefined)
  const [totalPages, setTotalPages] = useState<number>(0)

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  const [openConfirm, setOpenConfirm] = useState<boolean>(false)
  const [confirm, setConfirm] = useState<ConfirmDialogProps>({
    open: openConfirm,
    handleConfirm: () => {
      setOpenConfirm(false)
    },
    handleClose: () => {
      setOpenConfirm(false)
    },
  })

  // const [comments, setComments] = useRecoilState(commentState)
  const [comments, setComments] = useState<any[]>([])

  // 댓글 데이터 복사본 리턴
  const cloneComments = useCallback(
    () => comments.slice(0, comments.length),
    [comments],
  )

  // 페이지 조회
  const getComments = useCallback(
    ({ _page, _mode }: ICommentSearchProps) => {
      let searchPage = _page
      let searchSize = pagingSize
      if (_mode === 'until') {
        searchSize = pagingSize * (_page + 1)
        searchPage = 0
      }

      commentService
        .list(boardNo, postsNo, searchSize, searchPage)
        .then(result => {
          setPage(_page)
          // setTotalPages(result.totalPages)
          setTotalPages(Math.ceil(result.groupElements / pagingSize))
          refreshCommentCount(result.totalElements)

          let arr = _mode === 'append' ? cloneComments() : []
          arr.push(...result.content)

          setComments(arr)
        })
    },
    [boardNo, cloneComments, postsNo, refreshCommentCount],
  )

  // 전체 조회
  const getAllComments = () => {
    commentService.all(boardNo, postsNo).then(result => {
      setPage(result.number)
      setTotalPages(result.totalPages)
      refreshCommentCount(result.totalElements)

      let arr = []
      arr.push(...result.content)

      setComments(arr)
    })
  }

  useEffect(() => {
    if (page === undefined) {
      getComments({ _page: 0, _mode: 'replace' })
    }
  }, [getComments, page])

  // 댓글 갱신
  const handleRefresh = useCallback(() => {
    // getComments({ _page: 0, _mode: 'replace' }) // 첫페이지 재조회
    getComments({ _page: page, _mode: 'until' }) // 현재 페이지까지 재조회
  }, [getComments, page])

  // 댓글 상태 초기화
  const initComments = useCallback(() => {
    let arr: IComment[] = cloneComments()

    while (true) {
      const index = arr.findIndex(a => a.mode === 'reply' || a.mode === 'edit')
      if (index === -1) break

      if (arr[index].mode === 'reply') {
        arr.splice(index, 1)
      } else {
        arr[index].mode = 'none'
      }
    }

    return arr
  }, [cloneComments])

  // 성공 callback
  const successCallback = useCallback(() => {
    setSuccessSnackBar('success')

    // handleRefresh()

    let arr: IComment[] = initComments()
    setComments(arr)
  }, [initComments, setSuccessSnackBar])

  // 에러 callback
  const errorCallback = useCallback(
    (error: AxiosError) => {
      setErrorState({
        error,
      })
    },
    [setErrorState],
  )

  // 댓글 더보기
  const handleCommentMore = () => {
    getComments({ _page: page + 1, _mode: 'append' })
  }

  // 댓글 답글쓰기
  const handleCommentReply = async (parentCommentNo: number) => {
    let arr: IComment[] = initComments()

    const parentIndex = arr.findIndex(a => a.commentNo === parentCommentNo)

    const reply: IComment = {
      boardNo,
      postsNo,
      groupNo: arr[parentIndex].groupNo,
      parentCommentNo,
      depthSeq: arr[parentIndex].depthSeq + 1,
      createdBy: user.userId,
      createdName: user.userName,
      commentContent: '',
      mode: 'reply',
    }

    arr.splice(parentIndex + 1, 0, reply)

    setComments(arr)
  }

  // 댓글 수정
  const handleCommentEdit = async (commentNo: number) => {
    let arr: IComment[] = initComments()

    const index = arr.findIndex(a => a.commentNo === commentNo)

    arr[index].mode = 'edit'
    setComments(arr)
  }

  // 댓글 삭제
  const handleCommentDelete = async (commentNo: number) => {
    setConfirm({
      open: openConfirm,
      contentText: t('msg.confirm.delete'),
      handleConfirm: () => {
        setOpenConfirm(false)

        commentService.delete({
          boardNo,
          postsNo,
          commentNo,
          callback: successCallback,
          errorCallback,
        })
      },
      handleClose: () => {
        setOpenConfirm(false)
      },
    })
    setOpenConfirm(true)
  }

  // handleSubmit 댓글 저장
  const handleCommentSave = async (comment: IComment) => {
    if (comment.commentNo > 0) {
      await commentService.update({
        callback: () => {
          successCallback()

          getComments({ _page: page, _mode: 'until' }) // 현재 페이지까지 재조회
        },
        errorCallback,
        data: comment,
      })
    } else {
      await commentService.save({
        callback: () => {
          successCallback()

          if (comment.parentCommentNo) {
            getComments({ _page: page, _mode: 'until' }) // 현재 페이지까지 재조회
          } else {
            getAllComments() // 마지막 페이지까지 조회
          }
        },
        errorCallback,
        data: comment,
      })
    }
  }

  // 취소
  const handleCommentCancel = async () => {
    let arr: IComment[] = initComments()

    setComments(arr)
  }

  return (
    <Box boxShadow={1} className={classes.commentRoot}>
      <Box className={classes.commentTitle}>
        <Typography variant="h4" component="h3" className={classes.pdtb1}>
          {t('comment')}
        </Typography>
        <Link
          href="#"
          className={classNames({
            [classes.black]: true,
            [classes.ml1]: true,
            [classes.pdtb1]: true,
          })}
          onClick={(event: React.MouseEvent<HTMLAnchorElement>) => {
            event.preventDefault()
            handleRefresh()
          }}
        >
          <RefreshIcon fontSize="small" className={classes.commentIcon} />
        </Link>
      </Box>
      {comments &&
        comments.map(comment => {
          if (comment.mode !== 'edit' && comment.mode !== 'reply') {
            let buttons = []

            if (commentUseAt && deleteAt === 0) {
              buttons.push({
                label: t('label.button.reply'),
                size: 'small',
                handleButton: () => {
                  handleCommentReply(comment.commentNo)
                },
              })

              if (user?.userId === comment.createdBy) {
                buttons.push({
                  label: t('label.button.edit'),
                  size: 'small',
                  handleButton: () => {
                    handleCommentEdit(comment.commentNo)
                  },
                })
                buttons.push({
                  label: t('label.button.delete'),
                  size: 'small',
                  handleButton: () => {
                    handleCommentDelete(comment.commentNo)
                  },
                  completeMessage: t('msg.success.delete'),
                })
              }
            }

            return (
              <Card
                key={`comment${comment.commentNo}`}
                className={classNames({
                  [classes.commentBox]: true,
                })}
                style={{
                  paddingLeft: `${
                    theme.spacing(2) + comment.depthSeq * theme.spacing(4)
                  }px`,
                }}
              >
                <CardContent className={classes.commentView}>
                  <Typography gutterBottom variant="h6" component="h4">
                    {comment.createdName}
                  </Typography>
                  {comment.deleteAt !== 0 && (
                    <>
                      <ErrorOutlineIcon
                        fontSize="small"
                        className={classes.commentIcon}
                      />
                      {comment.deleteAt === 1 && t('common.delete.creator')}
                      {comment.deleteAt === 2 && t('common.delete.manager')}
                    </>
                  )}
                  <Typography variant="body2" color="textPrimary" component="p">
                    <Box
                      className={classNames({
                        [classes.commentContent]: true,
                        [classes.cancel]: comment.deleteAt !== 0,
                      })}
                      component="span"
                    >
                      {comment.commentContent}
                    </Box>
                  </Typography>
                </CardContent>
                <CardActions className={classes.commentView}>
                  <Typography
                    variant="body2"
                    color="textSecondary"
                    component="p"
                    className={classes.commentDate}
                  >
                    {comment.createdDate
                      ? convertStringToDateFormat(
                          comment.createdDate,
                          'yyyy-MM-dd HH:mm:ss',
                        )
                      : ''}
                  </Typography>
                  {comment.deleteAt === 0 && (
                    <CustomButtons buttons={buttons} className="mg0" />
                  )}
                </CardActions>
              </Card>
            )
          }

          return (
            <Box
              key={`comment${comment.commentNo}`}
              boxShadow={1}
              className={classes.pd1}
            >
              <CommentForm
                boardNo={boardNo}
                postsNo={postsNo}
                commentNo={comment.commentNo}
                commentContent={comment.commentContent}
                groupNo={comment.groupNo}
                parentCommentNo={comment.parentCommentNo}
                depthSeq={comment.depthSeq}
                handleCommentSave={handleCommentSave}
                handleCommentCancel={handleCommentCancel}
              />
            </Box>
          )
        })}
      <Box className={classes.moreBox} hidden={page + 1 >= totalPages}>
        <Button
          startIcon={<ExpandMoreIcon />}
          endIcon={<ExpandMoreIcon />}
          onClick={handleCommentMore}
        >
          {t('common.more')}
        </Button>
      </Box>
      {commentUseAt && deleteAt === 0 && (
        <Box className={classes.pdtb1}>
          <CommentForm
            handleCommentSave={handleCommentSave}
            boardNo={boardNo}
            postsNo={postsNo}
          />
        </Box>
      )}
      <ConfirmDialog
        open={openConfirm}
        contentText={confirm.contentText}
        handleClose={confirm.handleClose}
        handleConfirm={confirm.handleConfirm}
      />
    </Box>
  )
}

export { Comment }
