import { COMMENTS_MAX_LENGTH } from '@constants'
import useTextarea from '@hooks/useTextarea'
import { CommentSavePayload } from '@service'
import { userAtom } from '@stores'
import React, { createRef, forwardRef, useImperativeHandle } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

export type EditCommentsType = {
  clear: () => void
  textValue: ValueType | ReadonlyArray<string>
}

export interface EditCommentsProps {
  handleRegist: (comment: CommentSavePayload) => void
  handleCancel: () => void
  comment: CommentSavePayload
}

const EditComments = forwardRef<EditCommentsType, EditCommentsProps>(
  (props: EditCommentsProps, ref) => {
    const { handleRegist, handleCancel, comment } = props
    const { t } = useTranslation()
    const user = useRecoilValue(userAtom)
    const commentContentRef = createRef<HTMLTextAreaElement>()
    const { currentCount, clear, ...textarea } = useTextarea({
      value: '',
      currentCount: 0,
    })

    const handleRegistClick = (e: React.MouseEvent<HTMLButtonElement>) => {
      e.preventDefault()

      const commentContent = textarea.value as string
      if (commentContent.trim().length === 0) {
        commentContentRef.current?.focus()
        return
      }

      comment.commentContent = commentContent.trim()
      handleRegist(comment)
      handleCancel()
    }

    const handleCancelClick = (e: React.MouseEvent<HTMLAnchorElement>) => {
      e.preventDefault()
      handleCancel()
    }

    useImperativeHandle(ref, () => ({
      clear,
      textValue: textarea.value,
    }))

    return (
      <div>
        <div className="writeComment">
          <h5>{user.userName}</h5>
          <textarea
            ref={commentContentRef}
            placeholder={t('posts.reply_placeholder')}
            {...textarea}
          />
          <div className="currentCount">
            <span>{currentCount}</span> /<span>{COMMENTS_MAX_LENGTH}</span>
          </div>
          <div className="upload">
            <button onClick={handleRegistClick}>{t('label.button.reg')}</button>
            <a href="#" onClick={handleCancelClick}>
              {t('label.button.cancel')}
            </a>
          </div>
        </div>
      </div>
    )
  },
)

export { EditComments }
