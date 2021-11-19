import AttachList from '@components/AttachList'
import { CustomButtons, IButtonProps } from '@components/Buttons'
import CustomAlert from '@components/CustomAlert'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Editor from '@components/Editor'
import { Upload, UploadType } from '@components/Upload'
import Box from '@material-ui/core/Box'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import Grid from '@material-ui/core/Grid'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import {
  BoardSavePayload,
  boardService,
  fileService,
  IAttachmentResponse,
  PostsSavePayload,
  postsService,
  SKINT_TYPE_CODE_FAQ,
  SKINT_TYPE_CODE_QNA,
  UploadInfoReqeust,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { format } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useEffect, useRef, useState } from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
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
    switchBox: {
      padding: theme.spacing(1, 0),
    },
    switch: {
      paddingTop: theme.spacing(1),
      paddingBottom: theme.spacing(1),
      paddingLeft: theme.spacing(2),
      paddingRight: theme.spacing(2),
    },
    buttonContainer: {
      display: 'flex',
      margin: theme.spacing(1),
      justifyContent: 'center',
      '& .MuiButton-root': {
        margin: theme.spacing(1),
      },
    },
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
    labelMultiline: {
      padding: theme.spacing(2),
      textAlign: 'center',
      backgroundColor: theme.palette.background.default,
      height: '100%',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
    },
    upload: {
      padding: theme.spacing(2, 2, 0, 2),
    },
  }),
)

interface IPostsFormInput {
  postsTitle: string
  noticeAt: boolean
  postsContent: string
  postsAnswerContent: string
}

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
  const uploadRef = useRef<UploadType>()

  const [attachData, setAttachData] = useState<
    IAttachmentResponse[] | undefined
  >(undefined)

  // alert
  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
    handleAlert: () => setCustomAlert({ open: false }),
  })

  // Editor
  const [postsContent, setPostsContent] = useState<string>(
    initData?.postsContent || '',
  )
  const [postsAnswerContent, setPostsAnswerContent] = useState<string>(
    initData?.postsAnswerContent || '',
  )

  // form hook
  const methods = useForm<IPostsFormInput>({
    defaultValues: {
      postsTitle: initData?.postsTitle || '',
      noticeAt:
        typeof initData?.noticeAt !== 'undefined' ? initData?.noticeAt : false,
    },
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
  } = methods

  const successCallback = () => {
    setSuccessSnackBar('success')

    route.back()
  }

  const errorCallback = (error: AxiosError) => {
    setSuccessSnackBar('none')

    setErrorState({
      error,
    })
  }

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

  // handleSubmit 저장
  const handleSave = async (formData: IPostsFormInput) => {
    setSuccessSnackBar('loading')
    let { attachmentCode } = initData
    try {
      const postsContentValue = board.editorUseAt
        ? postsContent
        : formData.postsContent

      if (!postsContentValue) {
        setCustomAlert({
          open: true,
          message: format(t('valid.required.format'), [
            t('posts.posts_content'),
          ]),
          handleAlert: () => {
            setCustomAlert({ open: false })
          },
        })
        return
      }

      if (board.uploadUseAt) {
        const isUpload = await uploadRef.current.isModified(attachData)

        if (isUpload) {
          const info: UploadInfoReqeust = {
            entityName: 'posts',
            entityId: board.boardNo?.toString(),
          }

          // 업로드 및 저장
          const result = await uploadRef.current.upload(info, attachData)
          if (result) {
            if (result !== 'no attachments' && result !== 'no update list') {
              attachmentCode = result
            }
          }
        }
      }

      const data: PostsSavePayload = {
        boardNo,
        postsTitle: formData.postsTitle,
        noticeAt: formData.noticeAt,
        postsContent: postsContentValue,
        postsAnswerContent: board.editorUseAt
          ? postsAnswerContent
          : formData.postsAnswerContent,
        attachmentCode,
      }

      if (postsNo === -1) {
        await postsService.save({
          boardNo,
          callback: successCallback,
          errorCallback,
          data,
        })
      } else {
        await postsService.update({
          boardNo,
          postsNo,
          callback: successCallback,
          errorCallback,
          data,
        })
      }
    } catch (error) {
      setErrorState({
        error,
      })

      if (postsNo === -1) {
        uploadRef.current?.rollback(attachmentCode)
      }
    }
  }

  // 저장 버튼
  const saveButton: IButtonProps = {
    label: t('label.button.save'),
    variant: 'contained',
    color: 'primary',
    confirmMessage: t('msg.confirm.save'),
    handleButton: handleSubmit(handleSave),
  }

  // 이전 화면으로 이동
  const handlePrev = useCallback(() => {
    /* if (postsNo === -1) {
      route.push(
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
      )
    } else {
      route.push(
        {
          pathname: `/posts/${boardNo}/view/${postsNo}`,
          query: {
            size: route.query.size,
            page: route.query.page,
            keywordType: route.query.keywordType,
            keyword: route.query.keyword,
          },
        },
        // `/posts/${boardNo}`,
      )
    } */
    route.back()
  }, [route])

  // 이전 버튼
  const prevButton: IButtonProps = {
    label: t('label.button.prev'),
    variant: 'contained',
    handleButton: handlePrev,
  }

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <Grid container spacing={1}>
          <Grid item xs={12} sm={12}>
            <Box boxShadow={1}>
              <Controller
                name="postsTitle"
                render={({ field }) => (
                  <TextField
                    autoFocus
                    label={t('posts.posts_title')}
                    name="postsTitle"
                    required
                    inputProps={{ maxLength: 100 }}
                    id="outlined-full-width"
                    placeholder={format(t('msg.placeholder.format'), [
                      t('posts.posts_title'),
                    ])}
                    fullWidth
                    variant="outlined"
                    {...field}
                  />
                )}
                control={control}
                rules={{ required: true, maxLength: 100 }}
              />
              {errors.postsTitle && (
                <ValidationAlert
                  fieldError={errors.postsTitle}
                  target={[100]}
                  label={t('posts.posts_title')}
                />
              )}
            </Box>
          </Grid>
          <Grid item xs={12} sm={12}>
            <Box boxShadow={1} className={classes.switchBox}>
              <FormControlLabel
                label={t('posts.notice_at')}
                labelPlacement="start"
                control={
                  <Controller
                    name="noticeAt"
                    control={control}
                    render={({ field: { onChange, ref, value } }) => (
                      <Switch
                        inputProps={{ 'aria-label': 'secondary checkbox' }}
                        onChange={onChange}
                        inputRef={ref}
                        checked={value}
                      />
                    )}
                  />
                }
              />
            </Box>
          </Grid>
          <Grid item xs={12} sm={12}>
            {board.editorUseAt && (
              <Editor contents={postsContent} setContents={setPostsContent} />
            )}
            {!board.editorUseAt && (
              <Box boxShadow={1}>
                <Controller
                  name="postsContent"
                  control={control}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <TextField
                      label={t('posts.posts_content')}
                      name="postsContent"
                      multiline
                      minRows={9.2}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('posts.posts_content'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.postsContent && (
                  <ValidationAlert
                    fieldError={errors.postsContent}
                    label={t('posts.posts_content')}
                  />
                )}
              </Box>
            )}
          </Grid>
          {(board.skinTypeCode === SKINT_TYPE_CODE_FAQ ||
            board.skinTypeCode === SKINT_TYPE_CODE_QNA) && (
            <Grid item xs={12} sm={12}>
              {board.editorUseAt && (
                <Editor
                  contents={postsAnswerContent}
                  setContents={setPostsAnswerContent}
                />
              )}
              {!board.editorUseAt && (
                <Box boxShadow={1}>
                  <Controller
                    name="postsAnswerContent"
                    control={control}
                    render={({ field }) => (
                      <TextField
                        label={t('posts.posts_answer_content')}
                        name="postsAnswerContent"
                        multiline
                        minRows={9.2}
                        id="outlined-full-width"
                        placeholder={format(t('msg.placeholder.format'), [
                          t('posts.posts_answer_content'),
                        ])}
                        fullWidth
                        variant="outlined"
                        {...field}
                      />
                    )}
                  />
                </Box>
              )}
            </Grid>
          )}
          {board.uploadUseAt && (
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Upload
                  ref={uploadRef}
                  multi
                  uploadLimitCount={board.uploadLimitCount}
                  uploadLimitSize={board.uploadLimitSize}
                  attachmentCode={initData.attachmentCode}
                  attachData={attachData}
                />
                {attachData && (
                  <AttachList data={attachData} setData={setAttachData} />
                )}
              </Box>
            </Grid>
          )}
        </Grid>
      </FormProvider>
      <CustomButtons buttons={[saveButton, prevButton]} />
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
  const postsNo = Number(query.id)

  let board = {}
  let data = {}

  try {
    if (postsNo !== -1) {
      const result = await postsService.get(boardNo, postsNo)
      if (result) {
        board = (await result.data.board) as BoardSavePayload
        data = (await result.data) as PostsSavePayload
      }
    } else {
      const result = await boardService.get(boardNo)
      if (result) {
        board = (await result.data) as BoardSavePayload
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
