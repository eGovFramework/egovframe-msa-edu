import AttachList from '@components/AttachList'
import { BottomButtons, IButtons } from '@components/Buttons'
import {
  CommentsList,
  EditComments,
  EditCommentsType,
} from '@components/Comments'
import { COMMENTS_PAGE_SIZE } from '@constants'
import { format as dateFormat } from '@libs/date'
import {
  boardService,
  CommentSavePayload,
  fileService,
  IAttachmentResponse,
  IBoard,
  ICommentSearchProps,
  IPosts,
  PostsReqPayload,
  SKINT_TYPE_CODE_NORMAL,
  SKINT_TYPE_CODE_QNA,
} from '@service'
import { errorStateSelector, userAtom } from '@stores'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, {
  createRef,
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue, useSetRecoilState } from 'recoil'
import CustomAlert, { CustomAlertPrpps } from '@components/CustomAlert'

interface AlertProps extends CustomAlertPrpps {
  message: string
}

interface BaordViewProps {
  post: IPosts
  board: IBoard
}

const BoardView = (props: BaordViewProps) => {
  const { post, board } = props
  const router = useRouter()
  const { t } = useTranslation()

  const user = useRecoilValue(userAtom)

  const replyRef = createRef<EditCommentsType>()

  const setErrorState = useSetRecoilState(errorStateSelector)

  const [customAlert, setCustomAlert] = useState<AlertProps | undefined>({
    open: false,
    message: '',
    handleAlert: () => {},
  })

  // 첨부파일
  const [attachList, setAttachList] = useState<IAttachmentResponse[]>(undefined)

  useEffect(() => {
    if (post.attachmentCode) {
      const getAttachments = async () => {
        try {
          const result = await fileService.getAttachmentList(
            post.attachmentCode,
          )
          if (result?.data) {
            setAttachList(result.data)
          }
        } catch (error) {
          setErrorState({ error })
        }
      }

      getAttachments()
    }

    return () => setAttachList(null)
  }, [post, setErrorState])

  const [page, setPage] = useState<number>(undefined)
  const [totalPages, setTotalPages] = useState<number>(0)
  const [commentCount, setCommentCount] = useState<number>(undefined)

  const [comments, setComments] = useState<CommentSavePayload[]>(undefined)

  // 댓글 데이터 복사본 리턴
  const cloneComments = useCallback(
    () => comments.slice(0, comments.length),
    [comments],
  )

  // 페이지 조회
  const getComments = useCallback(
    ({ boardNo, postsNo, _page, _mode }: ICommentSearchProps) => {
      let searchPage = _page
      let searchSize = COMMENTS_PAGE_SIZE
      if (_mode === 'until') {
        searchSize = COMMENTS_PAGE_SIZE * (_page + 1)
        searchPage = 0
      }

      boardService
        .getComments(boardNo, postsNo, searchSize, searchPage)
        .then(result => {
          setPage(_page)
          // setTotalPages(result.totalPages)
          setTotalPages(Math.ceil(result.groupElements / COMMENTS_PAGE_SIZE))
          setCommentCount(result.totalElements)

          let arr = _mode === 'append' ? cloneComments() : []
          arr.push(...result.content)

          setComments(arr)
        })
    },
    [cloneComments],
  )

  // 전체 조회
  const getAllComments = () => {
    boardService.getAllComments(post.boardNo, post.postsNo).then(result => {
      setPage(result.number)
      setTotalPages(result.totalPages)
      setCommentCount(result.totalElements)

      let arr = []
      arr.push(...result.content)

      setComments(arr)
    })
  }

  useEffect(() => {
    if (typeof post.postsNo === 'undefined') {
      setCustomAlert({
        open: true,
        message: t('err.entity.not.found'),
        handleAlert: () => {
          setCustomAlert({
            ...customAlert,
            open: false,
          })
          router.back()
        },
      })
    }
    if (post.postsNo) {
      getComments({
        boardNo: post.boardNo,
        postsNo: post.postsNo,
        _page: 0,
        _mode: 'replace',
      })
    }
  }, [post])

  // 댓글 등록
  const handleCommentRegist = (comment: CommentSavePayload) => {
    if (comment.commentNo > 0) {
      boardService.updateComment(comment).then(() => {
        getComments({
          boardNo: post.boardNo,
          postsNo: post.postsNo,
          _page: page,
          _mode: 'until',
        }) // 현재 페이지까지 재조회
      })
    } else {
      boardService.saveComment(comment).then(() => {
        if (comment.parentCommentNo) {
          getComments({
            boardNo: post.boardNo,
            postsNo: post.postsNo,
            _page: page,
            _mode: 'until',
          }) // 현재 페이지까지 재조회
        } else {
          getAllComments() // 마지막 페이지까지 조회
        }
      })
    }
  }

  // 댓글 삭제
  const handleCommentDelete = (comment: CommentSavePayload) => {
    boardService.deleteComment(comment).then(() => {
      getComments({
        boardNo: post.boardNo,
        postsNo: post.postsNo,
        _page: page,
        _mode: 'until',
      }) // 현재 페이지까지 재조회
    })
  }

  const handleCommentCancel = () => {
    replyRef.current?.clear()
  }

  const handleCommentMore = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault()
    getComments({
      boardNo: post.boardNo,
      postsNo: post.postsNo,
      _page: page + 1,
      _mode: 'append',
    })
  }

  // 이전글, 다음글 클릭
  const handleNearPostClick = nearPost => {
    router.push(
      `/board/${router.query.skin}/${router.query.board}/view/${nearPost.postsNo}?size=${router.query.size}&page=${router.query.page}&keywordType=${router.query.keywordType}&keyword=${router.query.keyword}`,
    )
  }

  // 버튼
  const bottomButtons = useMemo((): IButtons[] => {
    const buttons: IButtons[] = [
      {
        id: 'board-list-button',
        title: t('label.button.list'),
        href: `/board/${router.query.skin}/${router.query.board}`,
      },
    ]

    if (user && board.userWriteAt && post.createdBy === user.userId) {
      buttons.push({
        id: 'board-edit-button',
        title: t('label.button.edit'),
        href: router.asPath.replace('view', 'edit'),
        className: 'blue',
      })
      return buttons.reverse()
    }

    return buttons
  }, [
    t,
    router.query.skin,
    router.query.board,
    router.asPath,
    user,
    board.userWriteAt,
    post.createdBy,
  ])

  return (
    <div className="table_view01">
      <h4>
        {post.noticeAt ? `[${t('common.notice')}] ` : ''}
        {post.postsTitle}
      </h4>
      <div className="view">
        <div className="top">
          <dl>
            <dt>{t('common.written_by')}</dt>
            <dd>{post.createdName}</dd>
          </dl>
          <dl>
            <dt>{t('common.written_date')}</dt>
            <dd>{post.createdDate && dateFormat(new Date(post.createdDate), 'yyyy-MM-dd')}</dd>
          </dl>
          <dl>
            <dt>{t('common.read_count')}</dt>
            <dd>{post.readCount}</dd>
          </dl>
          {board.uploadUseAt && (
            <dl className="file">
              <dt>{t('common.attachment')}</dt>
              <dd>
                <AttachList
                  data={attachList}
                  setData={setAttachList}
                  readonly
                />
              </dd>
            </dl>
          )}
        </div>
        {board.skinTypeCode === SKINT_TYPE_CODE_QNA && (
          <div className="qna-box">
            <div className="qna-question">
              <p
                className="qna-content"
                dangerouslySetInnerHTML={{ __html: post.postsContent }}
              />
            </div>
            <div className="qna-answer">
              <p
                className="qna-content"
                dangerouslySetInnerHTML={{ __html: post.postsAnswerContent }}
              />
            </div>
          </div>
        )}
        {board.skinTypeCode !== SKINT_TYPE_CODE_QNA && (
          <div
            className="content"
            dangerouslySetInnerHTML={{ __html: post.postsContent }}
          />
        )}
      </div>
      {board.commentUseAt && (
        <div className="commentWrap">
          <dl>
            <dt>{t('comment')}</dt>
            <dd>{commentCount}</dd>
          </dl>
          {user && (
            <EditComments
              ref={replyRef}
              handleCancel={handleCommentCancel}
              handleRegist={handleCommentRegist}
              comment={
                {
                  boardNo: post.boardNo,
                  postsNo: post.postsNo,
                  depthSeq: 0,
                } as CommentSavePayload
              }
            />
          )}

          {comments?.length > 0 ? (
            <CommentsList
              comments={comments}
              handleRegist={handleCommentRegist}
              handleDelete={handleCommentDelete}
            />
          ) : null}
          {page + 1 >= totalPages ? null : (
            <a href="#" onClick={handleCommentMore}>
              {t('posts.see_more')}
            </a>
          )}
        </div>
      )}
      {board.skinTypeCode === SKINT_TYPE_CODE_NORMAL && (
        <div className="skip">
          <dl>
            <dt>{t('posts.prev_post')}</dt>
            <dd>
              {(!post.prevPosts[0] || post.prevPosts.length === 0) && (
                <span>{t('posts.notexists.prev')}</span>
              )}
              {post.prevPosts[0] && (
                <a
                  href="#"
                  onClick={event => {
                    event.preventDefault()
                    handleNearPostClick(post.prevPosts[0])
                  }}
                >
                  {post.prevPosts[0].postsTitle}
                </a>
              )}
            </dd>
          </dl>
          <dl className="next">
            <dt>{t('posts.next_post')}</dt>
            <dd>
              {(!post.nextPosts[0] || post.nextPosts.length === 0) && (
                <span>{t('posts.notexists.next')}</span>
              )}
              {post.nextPosts[0] && (
                <a
                  href="#"
                  onClick={event => {
                    event.preventDefault()
                    handleNearPostClick(post.nextPosts[0])
                  }}
                >
                  {post.nextPosts[0].postsTitle}
                </a>
              )}
            </dd>
          </dl>
        </div>
      )}
      <BottomButtons handleButtons={bottomButtons} />
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={customAlert.handleAlert}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const boardNo = Number(context.query.board)
  const postsNo = Number(context.query.id)
  const { keywordType, keyword } = context.query

  let board = {}
  let post = {}

  try {
    if (postsNo !== -1) {
      const result = await boardService.getPostById({
        boardNo,
        postsNo,
        keywordType,
        keyword,
      } as PostsReqPayload)
      if (result && result.data && result.data.board) {
        board = (await result.data.board) as IBoard
        post = (await result.data) as IPosts
      }
    } else {
      const result = await boardService.getBoardById(boardNo)
      if (result && result.data) {
        board = (await result.data) as IBoard
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
      board,
      post,
    },
  }
}

export default BoardView
