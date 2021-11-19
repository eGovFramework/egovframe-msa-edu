import { ConfirmDialog } from '@components/Confirm'
import CustomAlert from '@components/CustomAlert'
import DraggableTreeMenu from '@components/DraggableTreeMenu'
import TreeSubButtons from '@components/DraggableTreeMenu/TreeSubButtons'
import { findTreeItem } from '@components/DraggableTreeMenu/TreeUtils'
import { MenuEditForm } from '@components/EditForm'
import Button from '@material-ui/core/Button'
import ButtonGroup from '@material-ui/core/ButtonGroup'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import Grid from '@material-ui/core/Grid'
import MenuItem from '@material-ui/core/MenuItem'
import Paper from '@material-ui/core/Paper'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import AddIcon from '@material-ui/icons/Add'
import DeleteIcon from '@material-ui/icons/Delete'
import SettingsIcon from '@material-ui/icons/Settings'
import {
  codeService,
  ICode,
  IMenuInfoForm,
  IMenuSavePayload,
  IMenuTree,
  ISite,
  menuService,
} from '@service'
import {
  conditionAtom,
  detailButtonsSnackAtom,
  draggableTreeExpandedAtom,
  draggableTreeSelectedAtom,
  errorStateSelector,
  treeChangeNameAtom,
} from '@stores'
import produce from 'immer'
import { GetServerSideProps } from 'next'
import { useSnackbar } from 'notistack'
import React, { createContext, useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    paper: {
      padding: theme.spacing(2),
      background: theme.palette.background.default,
    },
    buttons: {
      marginTop: theme.spacing(1),
      marginBottom: theme.spacing(1),
    },
    info: {
      minHeight: 350,
      background: theme.palette.background.paper,
    },
  }),
)

export interface MenuProps {
  sites: ISite[]
  menuTypes?: ICode[]
}

const conditionKey = 'menu'

const defaultMenu: IMenuSavePayload = {
  name: 'newMenu',
  parentId: null,
  siteId: null,
  sortSeq: 1,
  level: 1,
  isShow: true,
  isUse: true,
}

interface ICustomAlertState {
  open: boolean
  message: string
}

export const MenuFormContext = createContext<{
  menuFormData: IMenuInfoForm
  setMenuFormDataHandler: (data: IMenuInfoForm) => void
}>({
  menuFormData: undefined,
  setMenuFormDataHandler: () => {},
})

const Menu = ({ sites, menuTypes }: MenuProps) => {
  const classes = useStyles()

  const { t } = useTranslation()

  const { enqueueSnackbar } = useSnackbar()

  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const setErrorState = useSetRecoilState(errorStateSelector)

  const [menuFormData, setMenuFormData] = useState<IMenuInfoForm>(undefined)
  const setMenuFormDataHandler = (data: IMenuInfoForm) => {
    setMenuFormData(data)
  }

  const [siteState, setSiteState] = useState<number>(
    +keywordState?.siteId || sites[0]?.id,
  )
  const setExpanded = useSetRecoilState(draggableTreeExpandedAtom)
  const [treeSelected, setTreeSelected] = useRecoilState(
    draggableTreeSelectedAtom,
  )
  const [treeChangeName, setTreeChangeName] = useRecoilState(treeChangeNameAtom)

  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  const [customAlertState, setCustomAlertState] = useState<ICustomAlertState>({
    open: false,
    message: '',
  })
  const [deleteConfirmState, setDeleteConfirmState] =
    useState<ICustomAlertState>({
      open: false,
      message: t('msg.confirm.delete'),
    })

  const { data, mutate, error } = menuService.getTreeMenus(siteState)

  useEffect(() => {
    if (treeSelected) {
      menuService
        .getMenu(treeSelected.menuId)
        .then(result => {
          setMenuFormDataHandler(result)
        })
        .catch(error => {
          setErrorState({ error })
        })
    }
  }, [treeSelected])

  useEffect(() => {
    if (treeChangeName.state === 'complete') {
      menuService
        .updateName(treeChangeName.id, treeChangeName.name)
        .then(result => {
          setTreeChangeName({
            state: 'none',
          })
          mutate().then(result => {
            const selected = findTreeItem(result, treeSelected.menuId, 'menuId')
            setTreeSelected(selected.item)
          })
        })
        .catch(error => {
          setErrorState({ error })
        })
    }
  }, [treeChangeName])

  const handleSiteChange = (event: React.ChangeEvent<{ value: unknown }>) => {
    setSiteState(event.target.value as number)
  }

  const handleSave = async (formData: IMenuInfoForm) => {
    setSuccessSnackBar('loading')
    try {
      const result = await menuService.update(treeSelected.menuId, formData)
      setSuccessSnackBar('success')
      if (result) {
        mutate()
      }
    } catch (error) {
      setErrorState({ error })
      setSuccessSnackBar('none')
    }
  }

  const handleAddClick = async (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    let addMenu: IMenuSavePayload = produce(defaultMenu, draft => {
      draft.siteId = siteState
      draft.sortSeq = data?.length + 1
      draft.name = t('menu.new_menu')
    })
    if (treeSelected) {
      addMenu = produce(addMenu, draft => {
        draft.parentId = treeSelected.menuId
        draft.level = treeSelected.level + 1
        draft.sortSeq =
          treeSelected.children.length > 0
            ? treeSelected.children[treeSelected.children.length - 1].sortSeq +
              1
            : 1
      })
    }

    try {
      const result = await menuService.save(addMenu)
      if (result) {
        mutate()
      }
    } catch (error) {
      setErrorState({ error })
    }
  }

  const handleDeleteClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    if (!treeSelected) {
      setCustomAlertState({
        open: true,
        message: t('menu.valid.delete'),
      })
      return
    }

    setDeleteConfirmState({
      ...deleteConfirmState,
      open: true,
    })
  }

  const handleChangeNameClick = (
    event: React.MouseEvent<HTMLButtonElement>,
  ) => {
    event.preventDefault()

    if (!treeSelected) {
      setCustomAlertState({
        open: true,
        message: t('menu.valid.change_name'),
      })
      return
    }

    setTreeChangeName({
      state: 'change',
      id: null,
      name: null,
    })
  }

  const handleExpand = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    setExpanded('expand')
  }

  const handleCollapse = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    setExpanded('collapse')
  }

  const handleDeselect = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    setTreeSelected(undefined)
  }

  const handleAlert = () => {
    setCustomAlertState({
      ...customAlertState,
      open: false,
    })
  }

  const handleConfirmClose = () => {
    setDeleteConfirmState({
      ...deleteConfirmState,
      open: false,
    })
  }

  const handleConfirm = async () => {
    handleConfirmClose()

    try {
      await menuService.delete(treeSelected.menuId)

      enqueueSnackbar(t('msg.success.delete'), {
        variant: 'success',
      })
      mutate()
      setTreeSelected(undefined)
    } catch (error) {
      setErrorState({ error })
    }
  }

  const handleTreeDnD = async (tree: IMenuTree[]) => {
    try {
      const result = await menuService.updateDnD(siteState, tree)

      mutate()
    } catch (error) {
      setErrorState({ error })
    }
  }

  return (
    <div className={classes.root}>
      <Grid container spacing={2}>
        <Grid item sm={12} md={4}>
          <Paper className={classes.paper}>
            <Select fullWidth value={siteState} onChange={handleSiteChange}>
              {sites?.map(item => (
                <MenuItem key={item.id} value={item.id}>
                  {item.name}
                </MenuItem>
              ))}
            </Select>
            <ButtonGroup
              className={classes.buttons}
              size="small"
              aria-label="menu tree buttons"
            >
              <Button color="primary" onClick={handleAddClick}>
                <AddIcon fontSize="small" />
                {t('label.button.add')}
              </Button>
              <Button onClick={handleChangeNameClick}>
                <SettingsIcon fontSize="small" />
                {t('menu.update_name')}
              </Button>
              <Button color="secondary" onClick={handleDeleteClick}>
                <DeleteIcon fontSize="small" />
                {t('label.button.delete')}
              </Button>
            </ButtonGroup>
            {data && (
              <DraggableTreeMenu handleTreeDnD={handleTreeDnD} data={data} />
            )}
            <TreeSubButtons
              handleExpand={handleExpand}
              handleCollapse={handleCollapse}
              handleDeselect={handleDeselect}
            />
          </Paper>
          <CustomAlert
            contentText={customAlertState.message}
            open={customAlertState.open}
            handleAlert={handleAlert}
          />
          <ConfirmDialog
            open={deleteConfirmState.open}
            contentText={deleteConfirmState.message}
            handleClose={handleConfirmClose}
            handleConfirm={handleConfirm}
          />
        </Grid>
        <Grid item sm={12} md={8}>
          <MenuFormContext.Provider
            value={{ menuFormData, setMenuFormDataHandler }}
          >
            <Paper className={classes.paper}>
              {treeSelected ? (
                <MenuEditForm handleSave={handleSave} menuTypes={menuTypes} />
              ) : (
                <Card className={classes.info}>
                  <CardContent>
                    <Typography gutterBottom variant="h2">
                      Tip. 메뉴 관리
                    </Typography>
                    <Typography variant="body2" component="p">
                      1. 왼쪽트리메뉴에서 메뉴를 선택 하시면 해당메뉴의 정보를
                      조회/관리할 수 있습니다.
                      <br />
                      2. 드래그앤드랍 으로 선택된 메뉴를 이동시킬 수 있습니다.
                      <br />
                      3. 메뉴정보를 편집하시려면 메뉴를 선택하세요.
                    </Typography>
                  </CardContent>
                </Card>
              )}
            </Paper>
          </MenuFormContext.Provider>
        </Grid>
      </Grid>
    </div>
  )
}
export const getServerSideProps: GetServerSideProps = async context => {
  let sites: ISite[] = []
  let menuTypes: ICode[] = []

  try {
    const result = await menuService.getSites()

    if (sites) {
      sites = result
    }

    const codeDetails = await codeService.getCodeDetailList('menutype')
    if (codeDetails) {
      menuTypes = codeDetails.data as ICode[]
    }
  } catch (error) {
    console.error(`menu  getServerSideProps error ${error.message}`)
  }

  return {
    props: {
      sites,
      menuTypes,
    },
  }
}

export default Menu
