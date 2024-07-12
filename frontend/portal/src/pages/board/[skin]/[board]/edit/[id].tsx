import { NormalEditForm } from '@components/EditForm'
import {
  boardService,
  fileService,
  IAttachmentResponse,
  IBoard,
  IPosts,
  IPostsForm,
  PostsReqPayload,
  SKINT_TYPE_CODE_NORMAL,
  SKINT_TYPE_CODE_QNA, UploadInfoReqeust,
} from '@service'
import { errorStateSelector } from '@stores'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import React, { createContext, useCallback, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'
import CustomAlert, { CustomAlertPrpps } from '@components/CustomAlert'
import { UploadType } from '@components/Upload'
import produce from 'immer'

interface AlertProps extends CustomAlertPrpps {
  message: string
}

interface BoardEditProps {
  post: IPosts
  board: IBoard
}

export const BoardFormContext = createContext<{
  post: IPostsForm
  board: IBoard
  attachList: IAttachmentResponse[]
  setPostDataHandler: (data: IPostsForm) => void
  setAttachListHandler: (data: IAttachmentResponse[]) => void
  setUploaderHandler: (uploadType: React.MutableRefObject<UploadType>) => void
}>({
  post: undefined,
  board: undefined,
  attachList: undefined,
  setPostDataHandler: () => {},
  setAttachListHandler: () => {},
  setUploaderHandler: () => {},
})

const BoardEdit = (props: BoardEditProps) => {
  const { post, board } = props
  const router = useRouter()

  const { t } = useTranslation()
  const setErrorState = useSetRecoilState(errorStateSelector)

  const [customAlert, setCustomAlert] = useState<AlertProps | undefined>({
    open: false,
    message: '',
    handleAlert: () => {},
  })

  const [postData, setPostData] = useState<IPostsForm>(undefined)
  const setPostDataHandler = (data: IPostsForm) => {
    setPostData(data)
  }
  const [attachList, setAttachList] = useState<IAttachmentResponse[]>(undefined)
  const setAttachListHandler = (data: IAttachmentResponse[]) => {
    setAttachList(data)
  }
  const [uploader, setUploader] = useState<React.MutableRefObject<UploadType>>(undefined)
  const setUploaderHandler = (uploadType: React.MutableRefObject<UploadType>) => {
    setUploader(uploadType)
  }

  // callback
  const errorCallback = useCallback(
    (error: AxiosError) => {
      setErrorState({
        error,
      })
    },
    [setErrorState],
  )
  const successCallback = useCallback(() => {
    router.back()
  }, [])

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
  }, [post])

  const save = useCallback((data: IPosts) => {
    if (post.postsNo === -1) {
      boardService.savePost({
        boardNo: post.boardNo,
        callback: successCallback,
        errorCallback,
        data,
      })
    } else {
      boardService.updatePost({
        boardNo: post.boardNo,
        postsNo: post.postsNo,
        callback: successCallback,
        errorCallback,
        data,
      })
    }
    // boardService.
  }, [post, successCallback, errorCallback])

  useEffect(() => {
    if (postData) {
      let data: IPosts = {
        boardNo: post.boardNo,
        postsNo: post.postsNo,
        ...postData,
      }

      if (board.uploadUseAt) {
        uploader.current.isModified(attachList)
          .then(isUpload => {
            if (isUpload === true) {
              const info: UploadInfoReqeust = {
                entityName: 'posts',
                entityId: board.boardNo?.toString(),
              }

              // 업로드 및 저장
              uploader.current.upload(info, attachList)
                .then(result => {
                  if (result) {
                    if (result !== 'no attachments' && result !== 'no update list') {
                      data = produce(data, draft => {
                        draft.attachmentCode = result
                      })
                      save(data)
                    }
                  }
                })
            }else{
              save(data)
            }
          })
      }else{
        save(data)
      }
    }
  }, [postData, attachList])

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
  }, [post])

  return (
    <div className="qnaWrite">
      <div className="table_write01">
        <span>{t('common.required_fields')}</span>
        <BoardFormContext.Provider
          value={{
            post,
            board,
            attachList,
            setPostDataHandler,
            setAttachListHandler,
            setUploaderHandler,
          }}
        >
          {board.skinTypeCode === SKINT_TYPE_CODE_NORMAL && (
            <NormalEditForm post={post} />
          )}
          {board.skinTypeCode === SKINT_TYPE_CODE_QNA && (
            <NormalEditForm post={post} />
          )}
          {/* <QnAEditForm /> */}
        </BoardFormContext.Provider>
      </div>
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
        post = {
          boardNo,
          postsNo,
        }
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

export default BoardEdit
