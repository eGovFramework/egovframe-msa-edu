import React, { useContext, useMemo, useRef } from 'react'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import { Controller, useForm } from 'react-hook-form'
import AttachList from '@components/AttachList'
import { BottomButtons, IButtons } from '@components/Buttons'
import Editor from '@components/Editor'
import Upload, { UploadType } from '@components/Upload'
import ValidationAlert from '@components/ValidationAlert'
import Divider from '@material-ui/core/Divider'
import { BoardFormContext } from '@pages/board/[skin]/[board]/edit/[id]'
import { IPostsForm } from '@service'

import { DLWrapper } from '@components/WriteDLFields'
import { EDITOR_MAX_LENGTH } from '@constants'
import { getTextLength } from '@utils'
import { userAtom } from '@stores'
import { useRecoilValue } from 'recoil'
import { EditFormProps } from '.'

type QnAEditFormProps = EditFormProps

const QnAEditForm = (props: QnAEditFormProps) => {
  const router = useRouter()

  const uploadRef = useRef<UploadType>()
  const { post, board, attachList, setPostDataHandler, setAttachListHandler } =
    useContext(BoardFormContext)

  // form hook
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<IPostsForm>()

  const { t, i18n } = useTranslation()
  const user = useRecoilValue(userAtom)

  const handleFormSubmit = (data: IPostsForm) => {
    setPostDataHandler(data)
  }

  const bottomButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'board-edit-save',
        title: t('label.button.save'),
        href: '',
        className: 'blue',
        handleClick: handleSubmit(handleFormSubmit),
      },
      {
        id: 'board-edit-list',
        title: t('label.button.list'),
        href: `/board/${router.query['skin']}/${router.query['board']}`,
      },
    ],
    [i18n],
  )

  return (
    <>
      <form>
        <div className="write">
          <Controller
            control={control}
            name="postsTitle"
            render={({ field, fieldState }) => (
              <DLWrapper
                title={t('posts.qna_title')}
                className="inputTitle"
                required={true}
                error={fieldState.error}
              >
                <input
                  type="text"
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('posts.qna_title')}
                />
              </DLWrapper>
            )}
            defaultValue={''}
            rules={{ required: true }}
          />
          <DLWrapper title={t('common.written_by')} required={true}>
            <input type="text" value={user.userName} readOnly />
          </DLWrapper>

          {board.uploadUseAt && (
            <dl>
              <dt>{t('common.attachment')}</dt>
              <dd>
                <Upload
                  ref={uploadRef}
                  multi
                  uploadLimitCount={board.uploadLimitCount}
                  uploadLimitSize={board.uploadLimitSize}
                  attachmentCode={post.attachmentCode}
                  attachData={attachList}
                />
                <Divider variant="fullWidth" />
                {attachList && (
                  <AttachList
                    data={attachList}
                    setData={setAttachListHandler}
                  />
                )}
              </dd>
            </dl>
          )}
          <dl>
            <dt className="import">{t('posts.qna_content')}</dt>
            <dd>
              {board.editorUseAt ? (
                <>
                  <Controller
                    control={control}
                    name="postsContent"
                    render={({ field }) => (
                      <Editor
                        contents={field.value}
                        setContents={field.onChange}
                      />
                    )}
                    rules={{ required: true }}
                    defaultValue={''}
                  />
                  {errors.postsContent && (
                    <ValidationAlert
                      fieldError={errors.postsContent}
                      label={t('posts.posts_content')}
                    />
                  )}
                </>
              ) : (
                <>
                  <Controller
                    control={control}
                    name="postsContent"
                    render={({ field }) => (
                      <>
                        <div>
                          <textarea {...field} />
                        </div>
                        <div>
                          <p className="byte">
                            <span> {getTextLength(field.value, 'char')} </span>{' '}
                            / {EDITOR_MAX_LENGTH}
                          </p>
                        </div>
                      </>
                    )}
                    defaultValue={''}
                    rules={{ required: true, maxLength: EDITOR_MAX_LENGTH }}
                  />

                  {errors.postsContent && (
                    <ValidationAlert
                      fieldError={errors.postsContent}
                      target={[EDITOR_MAX_LENGTH]}
                      label={t('posts.posts_content')}
                    />
                  )}
                </>
              )}
            </dd>
          </dl>
        </div>
      </form>
      <BottomButtons handleButtons={bottomButtons} />
    </>
  )
}

export { QnAEditForm }
