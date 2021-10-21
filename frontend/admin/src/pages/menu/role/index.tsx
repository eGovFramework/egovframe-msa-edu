import { DetailButtons } from '@components/Buttons/DetailButtons'
import CustomTreeView, { CustomTreeViewType } from '@components/CustomTreeView'
import TreeSubButtons from '@components/DraggableTreeMenu/TreeSubButtons'
import { HorizontalTabs } from '@components/Tabs'
import Box from '@material-ui/core/Box'
import Button from '@material-ui/core/Button'
import ButtonGroup from '@material-ui/core/ButtonGroup'
import MenuItem from '@material-ui/core/MenuItem'
import Paper from '@material-ui/core/Paper'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Tab from '@material-ui/core/Tab'
import CheckBoxIcon from '@material-ui/icons/CheckBox'
import CheckBoxOutlineBlankIcon from '@material-ui/icons/CheckBoxOutlineBlank'
import { IRole, ISite, menuService, roleService } from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { GetServerSideProps } from 'next'
import React, { createRef, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useSetRecoilState } from 'recoil'
import { menuRoleService } from 'src/service/MenuRole'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      '& .MuiButtonGroup-contained': {
        boxShadow: theme.shadows[0],
      },
    },
    paper: {
      display: 'flex',
      flexDirection: 'column',
      padding: theme.spacing(2),
      background: theme.palette.background.paper,
    },
    select: {
      minWidth: 150,
      maxWidth: 300,
    },
    buttons: {
      padding: theme.spacing(1, 0.5),
    },
    buttonGroup: {
      '& .MuiButton-containedSizeSmall': {
        padding: '4px 6px',
        fontSize: '0.8rem',
      },
      whiteSpace: 'nowrap',
      marginRight: theme.spacing(1),
    },
  }),
)

export interface MenuRoleProps {
  sites: ISite[]
  roles: IRole[]
}

const MenuRole = (props: MenuRoleProps) => {
  const { sites, roles } = props
  const classes = useStyles()
  const { t } = useTranslation()

  const treeViewRef = createRef<CustomTreeViewType>() //treeview Ref
  const setErrorState = useSetRecoilState(errorStateSelector)
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  const [tabs, setTabs] = useState<React.ReactNode>(undefined)
  const [siteState, setSiteState] = useState<number>(sites ? sites[0].id : null)
  const [roleState, setRoleState] = useState<string>(
    roles ? roles[0].roleId : '',
  )

  const [expanded, setExpanded] = useState<boolean>(null)

  const { data, mutate, error } = menuRoleService.search(roleState, siteState)

  useEffect(() => {
    if (roles) {
      const createTabs = roles.map(role => {
        return (
          <Tab
            label={role.roleName}
            value={role.roleId}
            key={`tab-${role.roleId}`}
          />
        )
      })
      setTabs(createTabs)
    }
  }, [roles])

  const handleTab = (roleId: string) => {
    setRoleState(roleId)
    mutate(data, false)
  }

  const handleSiteChange = (event: React.ChangeEvent<{ value: unknown }>) => {
    setSiteState(event.target.value as number)
  }

  const handleExpand = () => {
    setExpanded(true)
  }

  const handleCollapse = () => {
    setExpanded(false)
  }

  const handleAllChecked = () => {
    treeViewRef.current?.handleAllChecked(true)
  }

  const handleAllUnchecked = () => {
    treeViewRef.current?.handleAllChecked(false)
  }

  const handleSave = async () => {
    setSuccessSnackBar('loading')
    if (treeViewRef.current) {
      const tree = treeViewRef.current.getTreeData()
      try {
        const result = await menuRoleService.save(tree)
        setSuccessSnackBar('success')
        if (result) {
          mutate()
        }
      } catch (error) {
        setErrorState({ error })
        setSuccessSnackBar('none')
      }
    }
  }

  return (
    <div className={classes.root}>
      {tabs && (
        <HorizontalTabs tabs={tabs} init={roleState} handleTab={handleTab} />
      )}
      <Paper className={classes.paper}>
        <Select
          className={classes.select}
          value={siteState}
          onChange={handleSiteChange}
        >
          {sites?.map(item => (
            <MenuItem key={item.id} value={item.id}>
              {item.name}
            </MenuItem>
          ))}
        </Select>
        <Box className={classes.buttons}>
          <ButtonGroup
            className={classes.buttonGroup}
            size="small"
            aria-label="menu tree buttons"
            variant="contained"
          >
            <Button onClick={handleAllChecked}>
              <CheckBoxIcon fontSize="small" />
              {t('label.button.all_checked')}
            </Button>
            <Button onClick={handleAllUnchecked}>
              <CheckBoxOutlineBlankIcon fontSize="small" />
              {t('label.button.all_unchecked')}
            </Button>
          </ButtonGroup>
          <TreeSubButtons
            handleExpand={handleExpand}
            handleCollapse={handleCollapse}
          />
        </Box>
        {data && (
          <CustomTreeView
            ref={treeViewRef}
            data={data}
            isChecked={true}
            isAllExpanded={expanded}
          />
        )}
        <DetailButtons handleSave={handleSave} />
      </Paper>
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let sites: ISite[] = []
  let roles: IRole[] = []

  try {
    const siteResult = await menuService.getSites()

    if (siteResult) {
      sites = siteResult
    }

    const roleResult = await roleService.searchAll()
    if (roleResult) {
      roles = roleResult.data
    }
  } catch (error) {
    console.error(`menu role getServerSideProps error ${error.message}`)
  }

  return {
    props: {
      sites,
      roles,
    },
  }
}

export default MenuRole
