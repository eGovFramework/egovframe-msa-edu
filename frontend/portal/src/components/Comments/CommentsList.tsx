import React from 'react'
import { CommentSavePayload } from '@service'
import { ViewComments } from './ViewComments'
import { AddComments } from './AddComments'

interface CommentsListProps {
  handleRegist: (comment: CommentSavePayload) => void
  handleDelete: (comment: CommentSavePayload) => void
  comments: CommentSavePayload[]
}

const CommentsList = (props: CommentsListProps) => {
  const { handleRegist, handleDelete, comments } = props

  return (
    <>
      {comments.length > 0 ? (
        <ul>
          {comments.map(item => (
            <li key={`comments-li-${item.commentNo}`}>
              <div
                style={{
                  paddingLeft: `${item.depthSeq * 30}px`,
                }}
              >
                <div className="writtenComment">
                  <ViewComments handleDelete={handleDelete} comment={item} />
                </div>

                <div className="reply">
                  <AddComments
                    handleRegist={handleRegist}
                    parentComment={item}
                  />
                </div>
              </div>
            </li>
          ))}
        </ul>
      ) : null}
    </>
  )
}

export { CommentsList }
