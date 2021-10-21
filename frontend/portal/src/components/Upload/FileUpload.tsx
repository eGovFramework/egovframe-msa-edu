import { DEFAULT_ACCEPT_FILE_EXT } from '@constants'
import Hidden from '@material-ui/core/Hidden'
import { IFile } from '@service'
import React, { useContext, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { FileContext, UploadProps } from '.'

const FileUpload = (props: UploadProps) => {
  const { accept, multi } = props
  const { t } = useTranslation()

  const fileInputRef = useRef<HTMLInputElement>(null)
  const { selectedFiles, setSelectedFilesHandler } = useContext(FileContext)

  const handleChangeFiles = (event: React.ChangeEvent<HTMLInputElement>) => {
    const fileList = event.target.files
    let newSelectedFiles: IFile[] = []
    for (const key in fileList) {
      if (Object.prototype.hasOwnProperty.call(fileList, key)) {
        const item = fileList[key]
        newSelectedFiles.push({
          key: `${Math.random().toString(36).substr(2, 11)}`,
          file: item,
        })
      }
    }

    if (selectedFiles !== undefined) {
      newSelectedFiles = newSelectedFiles.concat(selectedFiles)
    }

    setSelectedFilesHandler(newSelectedFiles)
  }

  const handleInputClick = (e: React.MouseEvent<HTMLInputElement>) => {
    e.preventDefault()
    fileInputRef.current?.click()
  }
  return (
    <div className="file custom">
      <input
        ref={fileInputRef}
        accept={accept || DEFAULT_ACCEPT_FILE_EXT}
        onChange={handleChangeFiles}
        multiple={multi}
        type="file"
        id="file"
        placeholder={t('file.placeholder')}
      />
      <input
        type="text"
        placeholder={t('file.placeholder')}
        readOnly
        onClick={handleInputClick}
      />
      <Hidden xsDown>
        <label htmlFor="file">{t('file.search')}</label>
      </Hidden>
    </div>
  )
}

export default FileUpload
