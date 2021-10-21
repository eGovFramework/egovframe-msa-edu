import AttachList from '@components/AttachList'
import { BottomButtons, IButtons } from '@components/Buttons'
import Editor from '@components/Editor'
import Upload, { UploadType } from '@components/Upload'
import ValidationAlert from '@components/ValidationAlert'
import { DLWrapper } from '@components/WriteDLFields'
import { EDITOR_MAX_LENGTH } from '@constants'
import Divider from '@material-ui/core/Divider'
import Hidden from '@material-ui/core/Hidden'
import { BoardFormContext } from '@pages/board/[skin]/[board]/edit/[id]'
import { IPostsForm, UploadInfoReqeust } from '@service'
import { getTextLength } from '@utils'
import produce from 'immer'
import { useRouter } from 'next/router'
import React, { useContext, useMemo, useRef } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import { EditFormProps } from '.'

type NormalEditFormProps = EditFormProps

const NormalEditForm = (props: NormalEditFormProps) => {
  const router = useRouter()
  const { t } = useTranslation()

  const uploadRef = useRef<UploadType>()
  const { post, board, attachList, setPostDataHandler, setAttachListHandler } =
    useContext(BoardFormContext)

  // form hook
  const methods = useForm<IPostsForm>({
    defaultValues: {
      postsTitle: props.post?.postsTitle || '',
      postsContent: props.post?.postsContent || '',
      attachmentCode: props.post?.attachmentCode || '',
    },
  })
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = methods

  const handleFormSubmit = async (data: IPostsForm) => {
    if (board.uploadUseAt) {
      const isUpload = await uploadRef.current.isModified(attachList)

      if (isUpload) {
        const info: UploadInfoReqeust = {
          entityName: 'posts',
          entityId: board.boardNo?.toString(),
        }

        // 업로드 및 저장
        const result = await uploadRef.current.upload(info, attachList)
        if (result) {
          if (result !== 'no attachments' && result !== 'no update list') {
            data = produce(data, draft => {
              draft.attachmentCode = result
            })
          }
        }
      }
    }

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
        href: `/board/${router.query.skin}/${router.query.board}`,
      },
    ],
    [router.query.board, router.query.skin],
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
                title={t('posts.posts_title')}
                className="inputTitle"
                required
                error={fieldState.error}
              >
                <input
                  type="text"
                  value={field.value}
                  onChange={field.onChange}
                  placeholder={t('posts.posts_title')}
                />
              </DLWrapper>
            )}
            defaultValue=""
            rules={{ required: true }}
          />

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

          {board.editorUseAt ? (
            <>
              <Hidden smUp>
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
                  defaultValue=""
                />
                {errors.postsContent && (
                  <ValidationAlert
                    fieldError={errors.postsContent}
                    label={t('posts.posts_content')}
                  />
                )}
              </Hidden>
              <Hidden xsDown>
                <dl>
                  <dt className="import">{t('posts.posts_content')}</dt>
                  <dd>
                    <div>
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
                        defaultValue=""
                      />
                      {errors.postsContent && (
                        <ValidationAlert
                          fieldError={errors.postsContent}
                          label={t('posts.posts_content')}
                        />
                      )}
                    </div>
                  </dd>
                </dl>{' '}
              </Hidden>
            </>
          ) : (
            <dl>
              <dt className="import">{t('posts.posts_content')}</dt>
              <dd>
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
                          <span> {getTextLength(field.value, 'char')} </span> /{' '}
                          {EDITOR_MAX_LENGTH}
                        </p>
                      </div>
                    </>
                  )}
                  defaultValue=""
                  rules={{ required: true, maxLength: EDITOR_MAX_LENGTH }}
                />

                {errors.postsContent && (
                  <ValidationAlert
                    fieldError={errors.postsContent}
                    target={[EDITOR_MAX_LENGTH]}
                    label={t('posts.posts_content')}
                  />
                )}
              </dd>
            </dl>
          )}
        </div>
      </form>
      <BottomButtons handleButtons={bottomButtons} />
    </>
  )
}

export { NormalEditForm }
