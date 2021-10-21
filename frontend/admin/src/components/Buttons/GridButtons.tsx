import React, { useState } from 'react'

import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Box from '@material-ui/core/Box'
import { Button } from '@material-ui/core'
import { useTranslation } from 'react-i18next'
import { ConfirmPopover } from '@components/Confirm'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
  }),
)

export interface IGridButtonProps {
  id: string
  handleDelete?: (id: string | number) => void
  handleUpdate?: (id: string | number) => void
}

const GridButtons: React.FC<IGridButtonProps> = ({
  id,
  handleDelete,
  handleUpdate,
}) => {
  const classes = useStyles()
  const { t } = useTranslation()

  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null)

  const onClickUpdate = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    handleUpdate(id)
  }

  const onClickDelete = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    handleDelete(id)
  }

  const handlePopover = (target: HTMLButtonElement | null) => {
    setAnchorEl(target)
  }

  return (
    <div className={classes.root}>
      {handleUpdate && (
        <Box mr={0.5}>
          <Button
            variant="outlined"
            color="primary"
            size="small"
            onClick={onClickUpdate}
          >
            {t('label.button.edit')}
          </Button>
        </Box>
      )}
      {handleDelete && (
        <Box ml={0.5}>
          <Button
            variant="outlined"
            color="secondary"
            size="small"
            onClick={(event: React.MouseEvent<HTMLButtonElement>) => {
              handlePopover(event.currentTarget)
            }}
          >
            {t('label.button.delete')}
          </Button>
          <ConfirmPopover
            message={t('msg.confirm.delete')}
            anchorEl={anchorEl}
            handlePopover={handlePopover}
            handleConfirm={onClickDelete}
          />
        </Box>
      )}
    </div>
  )
}

export { GridButtons }
