import CustomAlert from '@components/CustomAlert'
import { DEFAULT_ACCEPT_FILE_EXT_TEXT } from '@constants'
import {
  AttachmentSavePayload,
  fileService,
  IAttachmentResponse,
  IFile,
  UploadInfoReqeust,
} from '@service'
import { format, formatBytes } from '@utils'
import axios from 'axios'
import { useTranslation } from 'next-i18next'
import React, {
  createContext,
  forwardRef,
  useImperativeHandle,
  useState,
} from 'react'
import FileList from './FileList'
import FileUpload from './FileUpload'

export type UploadType = {
  isModified: (list?: IAttachmentResponse[]) => Promise<boolean>
  count: (list?: IAttachmentResponse[]) => Promise<number>
  upload: (
    info?: UploadInfoReqeust,
    list?: IAttachmentResponse[],
  ) => Promise<string>
  rollback: (attachmentCode: string) => void
}

export interface UploadProps {
  accept?: string
  acceptText?: string
  multi?: boolean
  uploadLimitCount?: number
  uploadLimitSize?: number
  attachmentCode?: string
  attachData?: IAttachmentResponse[]
}

export const FileContext = createContext<{
  selectedFiles: IFile[]
  setSelectedFilesHandler: (files: IFile[]) => void
}>({
  selectedFiles: undefined,
  setSelectedFilesHandler: () => {},
})

const Upload = forwardRef<UploadType, UploadProps>((props, ref) => {
  const {
    attachmentCode,
    attachData,
    uploadLimitCount,
    uploadLimitSize,
    acceptText = DEFAULT_ACCEPT_FILE_EXT_TEXT,
  } = props
  const { t } = useTranslation()

  // alert
  const [customAlert, setCustomAlert] = useState<{
    open: boolean
    contentText: string
  }>({
    open: false,
    contentText: '',
  })

  const [spare, setSpare] = useState<IFile[]>(undefined)
  const [selectedFiles, setSelectedFiles] = useState<IFile[]>(undefined)
  const setSelectedFilesHandler = (files: IFile[]) => {
    // 파일 수 체크
    const uploadCount =
      (attachData ? attachData.filter(file => !file.isDelete).length : 0) +
      files.length
    if (uploadLimitCount && uploadCount > uploadLimitCount) {
      setCustomAlert({
        open: true,
        contentText: format(t('valid.upload_limit_count.format'), [
          uploadLimitCount,
        ]),
      })
      return
    }
    // 용량 체크
    if (uploadLimitCount) {
      const uploadSize = files.reduce(
        (accumulator, currentValue) => accumulator + currentValue.file.size,
        0,
      )
      if (uploadSize > uploadLimitSize) {
        setCustomAlert({
          open: true,
          contentText: format(t('valid.upload_limit_size.format'), [
            `${formatBytes(uploadLimitSize, 0)}`,
          ]),
        })
        return
      }
    }

    setSelectedFiles(files)
  }

  useImperativeHandle(ref, () => ({
    isModified: (list?: IAttachmentResponse[]) =>
      new Promise<boolean>(resolve => {
        if (selectedFiles?.length > 0) {
          resolve(true)
        }
        if (list?.filter(m => m.isDelete).length > 0) {
          resolve(true)
        }
        resolve(false)
      }),
    count: (list?: IAttachmentResponse[]) =>
      new Promise<number>(resolve => {
        resolve(
          (selectedFiles?.length ? selectedFiles?.length : 0) +
            (list ? list.filter(m => !m.isDelete).length : 0),
        )
      }),
    upload: (info?: UploadInfoReqeust, list?: IAttachmentResponse[]) =>
      new Promise<string>((resolve, reject) => {
        if (selectedFiles) {
          let saveList: AttachmentSavePayload[] = []

          if (list && list.length > 0) {
            list.map(item => {
              if (item.isDelete) {
                saveList.push({
                  uniqueId: item.id,
                  isDelete: item.isDelete,
                })
              }
            })
          }

          const baseUrl = axios.defaults.baseURL
          axios.defaults.baseURL = ''
          const result = fileService
            .upload({
              fileList: selectedFiles,
              attachmentCode: attachmentCode,
              info,
              list: saveList,
            })
            .then(response => {
              axios.defaults.baseURL = baseUrl
              setSelectedFiles(undefined)
              resolve(response.data)
            })
            .catch(error => {
              axios.defaults.baseURL = baseUrl
              setSelectedFiles(undefined)
              reject(error)
            })
        } else if (list) {
          let saveList: AttachmentSavePayload[] = []

          list.map(item => {
            if (item.isDelete) {
              saveList.push({
                uniqueId: item.id,
                isDelete: item.isDelete,
              })
            }
          })

          if (saveList.length <= 0) {
            resolve('no update list')
            return
          }

          // const baseUrl = axios.defaults.baseURL
          // axios.defaults.baseURL = ''
          fileService
            .save({
              attachmentCode: attachmentCode,
              info,
              list: saveList,
            })
            .then(response => {
              // axios.defaults.baseURL = baseUrl
              resolve(response.data)
            })
            .catch(error => {
              // axios.defaults.baseURL = baseUrl
              reject(error)
            })
        } else {
          resolve('no attachments')
        }
      }),
    rollback: async (attachmentCode: string) => {
      try {
        await fileService.deleteAll(attachmentCode)

        if (spare) {
          setSelectedFiles(spare)
          setSpare(undefined)
        }
      } catch (error) {
        console.error(`file rollback error : ${error.message}`)
      }
    },
  }))

  const handleAlert = () => {
    setCustomAlert({
      ...customAlert,
      open: false,
    })
  }

  return (
    <>
      <FileContext.Provider value={{ selectedFiles, setSelectedFilesHandler }}>
        <FileUpload {...props} />
        <FileList />
      </FileContext.Provider>
      <div className="custom">
        * {t('file.accept_ext')} : {acceptText}
        <br />
        {uploadLimitSize &&
          `* ${format(t('file.msg_limit.format'), [
            formatBytes(uploadLimitSize, 0),
          ])}`}
      </div>
      <CustomAlert handleAlert={handleAlert} {...customAlert} />
    </>
  )
})

export default Upload
