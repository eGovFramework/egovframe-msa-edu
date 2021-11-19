import { DetailButtons } from '@components/Buttons'
import DialogPopup from '@components/DialogPopup'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import CardHeader from '@material-ui/core/CardHeader'
import Divider from '@material-ui/core/Divider'
import FormControlLabel from '@material-ui/core/FormControlLabel'
import FormGroup from '@material-ui/core/FormGroup'
import FormHelperText from '@material-ui/core/FormHelperText'
import Paper from '@material-ui/core/Paper'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import TextField from '@material-ui/core/TextField'
import ToggleButton from '@material-ui/lab/ToggleButton'
import ToggleButtonGroup from '@material-ui/lab/ToggleButtonGroup'
import Board from '@pages/board'
import Content from '@pages/content'
import { MenuFormContext } from '@pages/menu'
import { ICode, IMenuInfoForm } from '@service'
import produce from 'immer'
import React, { useContext, useEffect, useState } from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { useTranslation } from 'react-i18next'
import ValidationAlert from './ValidationAlert'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    content: {
      padding: theme.spacing(1, 2),
    },
    buttons: {
      marginBottom: theme.spacing(1),
      '& .MuiToggleButtonGroup-grouped': {
        lineHeight: 1,
      },
    },
    searchButton: {
      whiteSpace: 'nowrap',
    },
    search: {
      display: 'flex',
      alignItems: 'center',
      boxShadow: theme.shadows[0],
    },
    select: {
      marginLeft: theme.spacing(1),
      flex: 1,
    },
    verticalDivider: {
      height: 28,
      margin: 4,
    },
  }),
)

export interface MenuEditFormProps {
  handleSave: (formData: IMenuInfoForm) => void
  menuTypes?: ICode[]
}

interface IConnectId {
  code?: number
  name?: string
  error?: boolean
}

const MenuEditForm = (props: MenuEditFormProps) => {
  const { handleSave, menuTypes } = props
  const classes = useStyles()
  const { t } = useTranslation()

  const { menuFormData, setMenuFormDataHandler } = useContext(MenuFormContext)

  //form hook
  const methods = useForm<IMenuInfoForm>()
  const {
    register,
    formState: { errors },
    control,
    handleSubmit,
    setValue,
    reset,
  } = methods

  const [blankState, setBlankState] = useState<boolean>(false)
  const [menuTypeState, setMenuTypeState] = useState<string>(
    menuTypes[0]?.codeId,
  )

  const [connectIdState, setConnectIdState] = useState<IConnectId>({})

  const [dialogOpen, setDialogOpen] = useState<boolean>(false)

  useEffect(() => {
    if (menuFormData) {
      reset(
        produce(menuFormData, draft => {
          if (draft) {
            draft.menuKorName = draft?.menuKorName || ''
            draft.menuEngName = draft?.menuEngName || ''
            draft.urlPath = draft?.urlPath || ''
            draft.subName = draft?.subName || ''
            draft.icon = draft?.icon || ''
            draft.description = draft?.description || ''
          }
        }),
      )

      setMenuTypeState(menuFormData?.menuType || 'empty')
      setBlankState(
        menuFormData?.isBlank === null ? false : menuFormData?.isBlank,
      )
      setConnectIdState({
        ...connectIdState,
        code: menuFormData?.connectId,
        name: menuFormData?.connectName,
      })
    }
  }, [menuFormData])

  const handleLinkType = (
    event: React.MouseEvent<HTMLElement>,
    newValue: boolean | null,
  ) => {
    if (newValue === null) return
    setBlankState(newValue)
  }

  const handleMenuType = (
    event: React.MouseEvent<HTMLElement>,
    newValue: string | null,
  ) => {
    if (newValue === null) return

    const formOptions = {
      shouldDirty: true,
      shouldValidate: false,
    }
    setValue('connectId', null, formOptions)
    setValue('urlPath', '', formOptions)
    setConnectIdState({
      code: null,
      name: '',
      error: false,
    })
    setMenuTypeState(newValue)
  }

  const handleDialogOpen = () => {
    setDialogOpen(true)
  }

  const handleDialogClose = () => {
    setDialogOpen(false)
  }

  const handlePopup = (data: any) => {
    if (data) {
      let codeKey = 'contentNo'
      let nameKey = 'contentName'

      if (menuTypeState === 'board') {
        codeKey = 'boardNo'
        nameKey = 'boardName'
      }

      setValue('connectId', data[codeKey], {
        shouldDirty: true,
        shouldValidate: true,
      })
      setConnectIdState({
        code: data[codeKey],
        name: data[nameKey],
        error: false,
      })
    }

    handleDialogClose()
  }

  const handleSaveBefore = (formData: IMenuInfoForm) => {
    formData = produce(formData, draft => {
      draft.menuType = menuTypeState
      draft.menuTypeName = menuTypes.find(
        item => item.codeId === menuTypeState,
      ).codeName
      draft.isBlank = blankState
    })
    handleSave(formData)
  }

  return (
    <FormProvider {...methods}>
      <form>
        <Card className={classes.root}>
          <CardHeader title={t('menu.info_title')} />
          <Divider />
          <CardContent className={classes.content}>
            <TextField
              fullWidth
              label={t('menu.no')}
              name="menuId"
              variant="outlined"
              value={menuFormData ? menuFormData.menuId : ''}
              disabled
              margin="dense"
            />
            <Controller
              name="menuKorName"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  fullWidth
                  label={t('menu.name')}
                  required
                  variant="outlined"
                  margin="dense"
                  inputRef={field.ref}
                  value={field.value}
                  error={!!fieldState.error}
                  {...field}
                />
              )}
              defaultValue={''}
              rules={{ required: true, maxLength: 100 }}
            />
            {errors.menuKorName && (
              <ValidationAlert
                fieldError={errors.menuKorName}
                target={[100]}
                label={t('menu.name')}
              />
            )}

            <Controller
              name="menuEngName"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  fullWidth
                  label={t('menu.eng_name')}
                  required
                  variant="outlined"
                  margin="dense"
                  inputRef={field.ref}
                  value={field.value}
                  error={!!fieldState.error}
                  {...field}
                />
              )}
              defaultValue={''}
              rules={{ required: true, maxLength: 200 }}
            />
            {errors.menuEngName && (
              <ValidationAlert
                fieldError={errors.menuEngName}
                target={[100]}
                label={t('menu.eng_name')}
              />
            )}

            <FormHelperText>{t('menu.type')}</FormHelperText>
            <ToggleButtonGroup
              aria-label="menu type button group"
              className={classes.buttons}
              value={menuTypeState}
              onChange={handleMenuType}
              exclusive
            >
              {menuTypes?.map(item => (
                <ToggleButton
                  key={`menuType-${item.codeId}`}
                  value={item.codeId}
                >
                  {item.codeName}
                </ToggleButton>
              ))}
            </ToggleButtonGroup>

            {(menuTypeState === 'inside' || menuTypeState === 'outside') && (
              <>
                <Controller
                  name="urlPath"
                  control={control}
                  render={({ field, fieldState }) => (
                    <TextField
                      fullWidth
                      label={t('menu.url_path')}
                      required
                      variant="outlined"
                      margin="dense"
                      inputRef={field.ref}
                      value={field.value}
                      error={!!fieldState.error}
                      {...field}
                    />
                  )}
                  defaultValue={''}
                  rules={{ required: true, maxLength: 200 }}
                />
                {menuTypeState === 'outside' && (
                  <FormHelperText>{t('menu.outside_link_help')}</FormHelperText>
                )}
                {errors.urlPath && (
                  <ValidationAlert
                    fieldError={errors.urlPath}
                    target={[200]}
                    label={t('menu.url_path')}
                  />
                )}
              </>
            )}

            {(menuTypeState === 'contents' || menuTypeState === 'board') && (
              <>
                <Paper component="div" className={classes.search}>
                  <TextField
                    variant="outlined"
                    fullWidth
                    margin="dense"
                    label={`${
                      menuTypes.find(item => item.codeId === menuTypeState)
                        .codeName
                    } ${t('common.select')}`}
                    error={!!connectIdState.error}
                    value={connectIdState.name || ''}
                    required
                    disabled
                  />
                  <input
                    name="connectId"
                    type="text"
                    hidden
                    value={connectIdState.code || ''}
                    {...register('connectId', { required: true })}
                  />

                  <Divider
                    className={classes.verticalDivider}
                    orientation="vertical"
                  />
                  <Button
                    className={classes.searchButton}
                    variant="contained"
                    onClick={handleDialogOpen}
                  >{`${
                    menuTypes.find(item => item.codeId === menuTypeState)
                      .codeName
                  } ${t('label.button.find')}`}</Button>

                  <DialogPopup
                    id="find-dialog"
                    children={
                      menuTypeState === 'contents' ? (
                        <Content handlePopup={handlePopup} />
                      ) : (
                        <Board handlePopup={handlePopup} />
                      )
                    }
                    handleClose={handleDialogClose}
                    open={dialogOpen}
                    title={`${
                      menuTypes.find(item => item.codeId === menuTypeState)
                        .codeName
                    } ${t('label.button.find')}`}
                  />
                </Paper>
                {errors.connectId && (
                  <ValidationAlert
                    fieldError={errors.connectId}
                    label={`${
                      menuTypes.find(item => item.codeId === menuTypeState)
                        .codeName
                    } ${t('common.select')}`}
                  />
                )}
              </>
            )}

            {menuTypeState !== 'empty' && (
              <>
                <FormHelperText>{t('menu.connect_type')}</FormHelperText>
                <ToggleButtonGroup
                  exclusive
                  aria-label="link type button group"
                  className={classes.buttons}
                  value={blankState}
                  onChange={handleLinkType}
                >
                  <ToggleButton value={false} aria-label={t('menu.self')}>
                    {t('menu.self')}
                  </ToggleButton>
                  <ToggleButton value={true} aria-label={t('menu.blank')}>
                    {t('menu.blank')}
                  </ToggleButton>
                </ToggleButtonGroup>
              </>
            )}

            <Controller
              name="subName"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  fullWidth
                  label={t('menu.sub_name')}
                  variant="outlined"
                  margin="dense"
                  inputRef={field.ref}
                  value={field.value}
                  error={!!fieldState.error}
                  {...field}
                />
              )}
              defaultValue={''}
              rules={{ maxLength: 200 }}
            />

            {errors.subName && (
              <ValidationAlert fieldError={errors.subName} target={[200]} />
            )}

            <Controller
              name="icon"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  fullWidth
                  label={t('menu.icon')}
                  variant="outlined"
                  margin="dense"
                  inputRef={field.ref}
                  value={field.value}
                  error={!!fieldState.error}
                  {...field}
                />
              )}
              defaultValue={''}
              rules={{ maxLength: 100 }}
            />

            {errors.icon && (
              <ValidationAlert fieldError={errors.icon} target={[100]} />
            )}

            <Controller
              name="description"
              control={control}
              render={({ field, fieldState }) => (
                <TextField
                  fullWidth
                  label={t('menu.description')}
                  variant="outlined"
                  margin="dense"
                  multiline={true}
                  maxRows={3}
                  minRows={2}
                  inputRef={field.ref}
                  value={field.value}
                  error={!!fieldState.error}
                  {...field}
                />
              )}
              defaultValue={''}
              rules={{ maxLength: 500 }}
            />

            {errors.description && (
              <ValidationAlert fieldError={errors.description} target={[500]} />
            )}

            <FormGroup row>
              <FormControlLabel
                control={
                  <Controller
                    name="isUse"
                    render={({ field: { onChange, ref, value } }) => (
                      <Switch
                        inputProps={{ 'aria-label': 'secondary checkbox' }}
                        onChange={onChange}
                        inputRef={ref}
                        checked={value}
                      />
                    )}
                    control={control}
                    defaultValue={
                      typeof menuFormData?.isUse !== 'undefined'
                        ? menuFormData?.isUse
                        : true
                    }
                  />
                }
                label={t('common.use_at')}
                labelPlacement="start"
              />
              <FormControlLabel
                control={
                  <Controller
                    name="isShow"
                    render={({ field: { onChange, ref, value } }) => (
                      <Switch
                        inputProps={{ 'aria-label': 'secondary checkbox' }}
                        onChange={onChange}
                        inputRef={ref}
                        checked={value}
                      />
                    )}
                    control={control}
                    defaultValue={
                      typeof menuFormData?.isShow !== 'undefined'
                        ? menuFormData?.isShow
                        : true
                    }
                  />
                }
                label={t('menu.show_at')}
                labelPlacement="start"
              />
            </FormGroup>
          </CardContent>
        </Card>
      </form>
      <DetailButtons handleSave={handleSubmit(handleSaveBefore)} />
    </FormProvider>
  )
}

export { MenuEditForm }
