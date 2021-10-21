import React, { createRef, useState } from 'react'

import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'
import { userAtom } from '@stores'
import { CommentSavePayload } from '@service'
import { EditComments, EditCommentsType } from './EditComments'

interface WriteByButtonProps {
  handleRegist: (comment: CommentSavePayload) => void
  parentComment: CommentSavePayload
}

const AddComments = (props: WriteByButtonProps) => {
  const { handleRegist, parentComment } = props
  const { t } = useTranslation()

  const user = useRecoilValue(userAtom)

  const commentsRef = createRef<EditCommentsType>()
  const [editState, setEditState] = useState<boolean>(false)

  const handleReply = (e: React.MouseEvent<HTMLAnchorElement>) => {
    e.preventDefault()
    setEditState(!editState)
  }

  const handleCancel = () => {
    commentsRef.current?.clear()
    setEditState(false)
  }

  return (
    <>
      <a href="#" onClick={handleReply}>
        {user && t('label.button.reply')}
      </a>
      {Boolean(editState) === true && (
        <div>
          {user && (
            <EditComments
              ref={commentsRef}
              handleRegist={handleRegist}
              handleCancel={handleCancel}
              comment={
                {
                  boardNo: parentComment.boardNo,
                  postsNo: parentComment.postsNo,
                  groupNo: parentComment.groupNo,
                  parentCommentNo: parentComment.commentNo,
                  depthSeq: parentComment.depthSeq + 1,
                } as CommentSavePayload
              }
            />
          )}
        </div>
      )}
    </>
  )
}

export { AddComments }
