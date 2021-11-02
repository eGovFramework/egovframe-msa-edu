import Chip from '@material-ui/core/Chip'
import Paper from '@material-ui/core/Paper'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import AssignmentReturnedIcon from '@material-ui/icons/AssignmentReturned'
import { fileService, IAttachmentResponse } from '@service'
import produce from 'immer'
import React from 'react'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      flexWrap: 'wrap',
      listStyle: 'none',
      padding: theme.spacing(0),
      margin: 0,
    },
    item: {
      padding: theme.spacing(1),
    },
    chip: {
      margin: theme.spacing(0.5),
    },
  }),
)

export interface AttachListProps {
  data: IAttachmentResponse[]
  setData?: React.Dispatch<React.SetStateAction<IAttachmentResponse[]>>
  readonly?: boolean
}

const AttachList = (props: AttachListProps) => {
  const { data, setData, readonly } = props
  const classes = useStyles()

  const handleClick = (item: IAttachmentResponse) => () => {
    fileService.download(item.id)
  }

  const handleDelete = (item: IAttachmentResponse) => () => {
    setData(
      produce(data, draft => {
        const idx = draft.findIndex(attachment => attachment.id === item.id)
        draft[idx].isDelete = true
      }),
    )
  }

  return (
    <Paper component="ul" className={classes.root}>
      {data &&
        data.map(item => {
          return item.isDelete ? null : (
            <li key={`li-${item.id}`} className={classes.item}>
              {readonly ? (
                <Chip
                  key={`chip-${item.id}`}
                  label={item.originalFileName}
                  onClick={handleClick(item)}
                  className={classes.chip}
                  icon={<AssignmentReturnedIcon />}
                />
              ) : (
                <Chip
                  key={`chip-${item.id}`}
                  label={item.originalFileName}
                  onClick={handleClick(item)}
                  onDelete={handleDelete(item)}
                  className={classes.chip}
                  icon={<AssignmentReturnedIcon />}
                />
              )}
            </li>
          )
        })}
    </Paper>
  )
}

export default AttachList
