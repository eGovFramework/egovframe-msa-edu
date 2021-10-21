import React from 'react'
import { useTranslation } from 'react-i18next'
import { format as dateFormat } from '@libs/date'
import { userAtom } from '@stores'
import { useRecoilValue } from 'recoil'
import { CommentSavePayload } from '@service'

interface ViewCommentsProps {
  handleDelete: (comment: CommentSavePayload) => void
  comment: CommentSavePayload
}

const ViewComments = (props: ViewCommentsProps) => {
  const { handleDelete, comment } = props
  const { t } = useTranslation()
  const user = useRecoilValue(userAtom)

  return (
    <>
      <div className="userName">
        <strong>{comment.createdName}</strong>
        <span>
          {dateFormat(new Date(comment.createdDate), 'yyyy-MM-dd HH:mm:ss')}
        </span>
      </div>
      <div className="commentContent">
        <div dangerouslySetInnerHTML={{ __html: comment.commentContent }} />
      </div>
      {user && user.userId === comment.createdBy ? (
        <a
          href="#"
          onClick={event => {
            event.preventDefault()
            handleDelete(comment)
          }}
        >
          {t('label.button.delete')}
        </a>
      ) : null}
    </>
  )
}

export { ViewComments }
