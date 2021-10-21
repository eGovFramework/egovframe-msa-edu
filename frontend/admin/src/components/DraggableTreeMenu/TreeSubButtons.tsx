import React from 'react'
import Button from '@material-ui/core/Button'
import ButtonGroup from '@material-ui/core/ButtonGroup'
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles'
import ExpandLessOutlinedIcon from '@material-ui/icons/ExpandLessOutlined'
import ExpandMoreOutlinedIcon from '@material-ui/icons/ExpandMoreOutlined'
import CheckBoxOutlineBlankOutlinedIcon from '@material-ui/icons/CheckBoxOutlineBlankOutlined'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      '& .MuiButton-containedSizeSmall': {
        padding: '4px 6px',
        fontSize: '0.8rem',
      },
      whiteSpace: 'nowrap',
    },
  }),
)

export interface TreeBelowButtonsProps {
  handleExpand: (event: React.MouseEvent<HTMLButtonElement>) => void
  handleCollapse: (event: React.MouseEvent<HTMLButtonElement>) => void
  handleDeselect?: (event: React.MouseEvent<HTMLButtonElement>) => void
}

const TreeSubButtons = (props: TreeBelowButtonsProps) => {
  const { handleExpand, handleCollapse, handleDeselect } = props
  const classes = useStyles()
  const { t } = useTranslation()
  return (
    <>
      <ButtonGroup
        className={classes.root}
        size="small"
        aria-label="menu tree buttons"
        variant="contained"
      >
        <Button onClick={handleExpand}>
          <ExpandMoreOutlinedIcon fontSize="small" />
          {t('menu.all_expand')}
        </Button>
        <Button onClick={handleCollapse}>
          <ExpandLessOutlinedIcon fontSize="small" />
          {t('menu.all_collapse')}
        </Button>
        {handleDeselect && (
          <Button onClick={handleDeselect}>
            <CheckBoxOutlineBlankOutlinedIcon fontSize="small" />
            {t('label.button.deselect')}
          </Button>
        )}
      </ButtonGroup>
    </>
  )
}

export default TreeSubButtons
