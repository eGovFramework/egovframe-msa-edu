import { DetailButtons } from '@components/Buttons'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import Box from '@material-ui/core/Box'
import FormControl from '@material-ui/core/FormControl'
import Grid from '@material-ui/core/Grid'
import InputLabel from '@material-ui/core/InputLabel'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import TextField from '@material-ui/core/TextField'
import {
  codeService,
  ICode,
  IRole,
  roleService,
  UserSavePayload,
  userService,
} from '@service'
import { detailButtonsSnackAtom, errorStateSelector } from '@stores'
import { format } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React from 'react'
import { Controller, FormProvider, useForm } from 'react-hook-form'
import { useSetRecoilState } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      marginTop: theme.spacing(1),
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(2),
      },
    },
    formControl: {
      width: '100%',
    },
    switch: {
      paddingTop: theme.spacing(1),
      paddingBottom: theme.spacing(1),
      paddingLeft: theme.spacing(2),
      paddingRight: theme.spacing(2),
    },
    buttonContainer: {
      display: 'flex',
      margin: theme.spacing(1),
      justifyContent: 'center',
      '& .MuiButton-root': {
        margin: theme.spacing(1),
      },
    },
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
  }),
)

interface IUserFormInput {
  email: string
  password: string
  passwordConfirm: string
  userName: string
  roleId: string
  userStateCode: string
}

export interface IUserItemsProps {
  userId: string
  initData: UserSavePayload | null
  roles: IRole[]
  userStateCodeList: ICode[]
}

const UserItem = ({
  userId,
  initData,
  roles,
  userStateCodeList,
}: IUserItemsProps) => {
  const classes = useStyles()
  const route = useRouter()
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 상태관리 hook
  const setErrorState = useSetRecoilState(errorStateSelector)

  // form hook
  const methods = useForm<IUserFormInput>({
    defaultValues: {
      email: initData?.email || '',
      password: '',
      passwordConfirm: '',
      userName: initData?.userName || '',
      roleId: initData?.roleId || 'ROLE_ANONYMOUS',
      userStateCode: initData?.userStateCode || '00',
    },
  })
  const {
    formState: { errors },
    control,
    handleSubmit,
  } = methods

  const successCallback = () => {
    setSuccessSnackBar('success')

    route.back()
  }

  const errorCallback = (error: AxiosError) => {
    setSuccessSnackBar('none')

    setErrorState({
      error,
    })
  }

  // handleSubmit 저장
  const handleSave = async (formData: IUserFormInput) => {
    setSuccessSnackBar('loading')

    const saved: UserSavePayload = {
      email: formData.email,
      password: formData.password,
      userName: formData.userName,
      roleId: formData.roleId,
      userStateCode: formData.userStateCode,
    }

    if (userId === '-1') {
      await userService.save({
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    } else {
      await userService.update({
        userId,
        callback: successCallback,
        errorCallback,
        data: saved,
      })
    }
  }

  // 비밀번호 형식 확인
  const checkPasswordPattern = value =>
    /^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,20}$/.test(value)

  return (
    <div className={classes.root}>
      <FormProvider {...methods}>
        <form>
          <Grid container spacing={1}>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="email"
                  control={control}
                  rules={{ required: true, maxLength: 100 }}
                  render={({ field }) => (
                    <TextField
                      autoFocus
                      label={t('user.email')}
                      name="email"
                      required
                      inputProps={{ maxLength: 100 }}
                      placeholder={format(t('msg.placeholder.format'), [
                        t('user.email'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.email && (
                  <ValidationAlert
                    fieldError={errors.email}
                    target={[100]}
                    label={t('user.email')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="password"
                  control={control}
                  rules={{
                    required: userId === '-1',
                    maxLength: {
                      value: 20,
                      message: format(t('valid.maxlength.format'), [20]),
                    },
                    validate: value =>
                      !value ||
                      checkPasswordPattern(value) ||
                      (t('valid.password') as string),
                  }}
                  render={({ field }) => (
                    <TextField
                      type="password"
                      label={t('user.password')}
                      name="password"
                      required={userId === '-1'}
                      inputProps={{ maxLength: 20 }}
                      placeholder={format(t('msg.placeholder.format'), [
                        t('user.password'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.password && (
                  <ValidationAlert
                    fieldError={errors.password}
                    target={[20]}
                    label={t('user.password')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="passwordConfirm"
                  control={control}
                  rules={{
                    required: userId === '-1',
                    maxLength: {
                      value: 20,
                      message: format(t('valid.maxlength.format'), [20]),
                    },
                    validate: value =>
                      (!methods.getValues().password && !value) ||
                      (checkPasswordPattern(value) &&
                        methods.getValues().password === value) ||
                      (t('valid.password.confirm') as string),
                  }}
                  render={({ field }) => (
                    <TextField
                      type="password"
                      label={t('label.title.password_confirm')}
                      name="passwordConfirm"
                      required={userId === '-1'}
                      inputProps={{ maxLength: 20 }}
                      placeholder={format(t('msg.placeholder.format'), [
                        t('label.title.password_confirm'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.passwordConfirm && (
                  <ValidationAlert
                    fieldError={errors.passwordConfirm}
                    target={[20]}
                    label={t('label.title.password_confirm')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <Box boxShadow={1}>
                <Controller
                  name="userName"
                  control={control}
                  rules={{ required: true, maxLength: 25 }}
                  render={({ field }) => (
                    <TextField
                      label={t('user.user_name')}
                      name="userName"
                      required
                      inputProps={{ maxLength: 25 }}
                      id="outlined-full-width"
                      placeholder={format(t('msg.placeholder.format'), [
                        t('user.user_name'),
                      ])}
                      fullWidth
                      variant="outlined"
                      {...field}
                    />
                  )}
                />
                {errors.userName && (
                  <ValidationAlert
                    fieldError={errors.userName}
                    target={[25]}
                    label={t('user.user_name')}
                  />
                )}
              </Box>
            </Grid>
            <Grid item xs={12} sm={12}>
              <FormControl variant="outlined" className={classes.formControl}>
                <InputLabel id="roleId-label" required>
                  {t('role')}
                </InputLabel>
                <Controller
                  name="roleId"
                  control={control}
                  defaultValue={initData?.roleId || 'ROLE_ANONYMOUS'}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <Select
                      variant="outlined"
                      name="roleId"
                      required
                      labelId="roleId-label"
                      label={t('role')}
                      margin="dense"
                      {...field}
                    >
                      {roles.map(option => (
                        <MenuItem key={option.roleId} value={option.roleId}>
                          {option.roleName}
                        </MenuItem>
                      ))}
                    </Select>
                  )}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={12}>
              <FormControl variant="outlined" className={classes.formControl}>
                <InputLabel id="userStateCode-label" required>
                  {t('user.user_state_code')}
                </InputLabel>
                <Controller
                  name="userStateCode"
                  control={control}
                  defaultValue={initData?.userStateCode || '00'}
                  rules={{ required: true }}
                  render={({ field }) => (
                    <Select
                      variant="outlined"
                      name="userStateCode"
                      required
                      labelId="roleId-label"
                      label={t('user.user_state_code')}
                      margin="dense"
                      {...field}
                    >
                      {userStateCodeList.map(option => (
                        <MenuItem key={option.codeId} value={option.codeId}>
                          {option.codeName}
                        </MenuItem>
                      ))}
                    </Select>
                  )}
                />
              </FormControl>
            </Grid>
          </Grid>
        </form>
      </FormProvider>
      <DetailButtons
        handleList={() => {
          route.back()
        }}
        handleSave={handleSubmit(handleSave)}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const userId = query.id

  let data = {}
  let roles: any[] = []
  let userStateCodeList = []

  try {
    const result = await roleService.searchAll()
    if (result) {
      roles = result.data
    }
  } catch (error) {
    console.error(`role query error ${error.message}`)
  }

  try {
    const codeList = await codeService.getCodeDetailList('user_state_code')
    if (codeList) {
      userStateCodeList = (await codeList.data) as ICode[]
    }
  } catch (error) {
    console.error(`codes query error ${error.message}`)
  }

  try {
    if (userId !== '-1') {
      const result = await userService.get(userId as string)
      if (result) {
        data = (await result.data) as UserSavePayload
      }
    }
  } catch (error) {
    console.error(`user info query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      userId,
      initData: data,
      roles,
      userStateCodeList,
    },
  }
}

export default UserItem
