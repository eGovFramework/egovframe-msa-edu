import React, { useState } from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { useTranslation } from 'next-i18next'

import {
  createStyles,
  makeStyles,
  Theme,
  useTheme,
} from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import TextField from '@material-ui/core/TextField'
import Button from '@material-ui/core/Button'

import useUser from '@hooks/useUser'
import { IComment } from '@service'
import { format } from '@utils'
import CustomAlert from '@components/CustomAlert'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    commentCreator: {
      padding: theme.spacing(1, 1, 1, 0),
      fontWeight: 500,
    },
    commentContent: {
      padding: theme.spacing(0),
    },
    commentContentInput: {
      padding: '0 !important',
    },
    commentButtons: {
      margin: theme.spacing(1, 0, 0, 0),
      padding: theme.spacing(0),
    },
    ml1: {
      marginLeft: theme.spacing(1),
    },
  }),
)

export interface ICommentFormProps {
  boardNo: number
  postsNo: number
  commentNo?: number
  commentContent?: string
  groupNo?: number
  parentCommentNo?: number
  depthSeq?: number
  handleCommentSave: (comment: ICommentFormInput) => void
  handleCommentCancel?: (comment: ICommentFormInput) => void
}

interface ICommentFormInput {
  parentCommentNo?: number
  commentContent: string
}

const CommentForm: React.FC<ICommentFormProps> = ({
  boardNo,
  postsNo,
  commentNo,
  commentContent,
  groupNo,
  parentCommentNo,
  depthSeq,
  handleCommentSave,
  handleCommentCancel,
}) => {
  const classes = useStyles()
  const { user } = useUser()
  const { t } = useTranslation()
  const theme = useTheme()

  // alert
  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
    handleAlert: () => setCustomAlert({ open: false }),
  })

  // form hook
  const methods = useForm<ICommentFormInput>({
    defaultValues: {
      commentContent,
    },
  })
  const { control, handleSubmit, setValue, setFocus } = methods

  const saveComment = async (formData: ICommentFormInput) => {
    if (!formData.commentContent) {
      setCustomAlert({
        open: true,
        message: format(t('valid.required.format'), [
          t('comment.comment_content'),
        ]),
        handleAlert: () => {
          setCustomAlert({
            open: false,
          })
          setFocus('commentContent') // TODO 작동안함..
        },
      })
      return
    }

    const comment: IComment = {
      boardNo,
      postsNo,
      commentNo,
      commentContent: formData.commentContent,
      groupNo,
      parentCommentNo,
      depthSeq: typeof depthSeq === 'undefined' ? 0 : depthSeq,
    }
    handleCommentSave(comment)

    if (!parentCommentNo && typeof commentNo === 'undefined') {
      setValue('commentContent', '')
    }
  }

  const handleCancel = () => {
    const comment: IComment = {
      boardNo,
      postsNo,
      commentNo,
      commentContent,
      groupNo,
      parentCommentNo,
      depthSeq,
    }
    if (handleCommentCancel) {
      handleCommentCancel(comment)
    }

    // setValue('commentContent', '')
  }

  return (
    <FormProvider {...methods}>
      <form
        style={{
          paddingLeft: `${theme.spacing(1) + depthSeq * theme.spacing(4)}px`,
        }}
      >
        <Box className={classes.commentCreator}>{user.userName}</Box>
        <Controller
          name="commentContent"
          control={control}
          rules={{ maxLength: 2000 }}
          render={({ field }) => (
            <TextField
              label={t('comment.comment_content')}
              className={classes.commentContent}
              multiline
              minRows={1}
              inputProps={{
                maxLength: 2000,
                className: classes.commentContentInput,
              }}
              id="outlined-full-width"
              placeholder={format(t('msg.placeholder.format'), [
                t('comment.comment_content'),
              ])}
              fullWidth
              variant="outlined"
              {...field}
            />
          )}
        />
        <Box
          className={classes.commentButtons}
          display="flex"
          justifyContent="flex-end"
          m={1}
          p={1}
        >
          <Button
            variant="contained"
            color="primary"
            size="small"
            onClick={handleSubmit(saveComment)}
          >
            {commentNo || parentCommentNo
              ? t('label.button.save')
              : t('label.button.reg')}
          </Button>
          {(commentNo || parentCommentNo) && (
            <Button
              className={classes.ml1}
              variant="contained"
              color="default"
              size="small"
              onClick={handleCancel}
            >
              {t('label.button.cancel')}
            </Button>
          )}
        </Box>
      </form>
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={customAlert.handleAlert}
      />
    </FormProvider>
  )
}

export { CommentForm }
