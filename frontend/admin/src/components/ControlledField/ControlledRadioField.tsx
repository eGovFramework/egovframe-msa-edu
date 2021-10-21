import React from 'react'
import { Controller } from 'react-hook-form'
import ValidationAlert from '@components/EditForm/ValidationAlert'
import RadioGroupField from '@components/RadioGroupField'
import { ControlledFieldProps } from '.'

interface ControlledRadioFieldProps extends ControlledFieldProps {
  name: any
  label: string
  defaultValue: string
  data: { idkey: string; namekey: string; data: any[] }
  requried?: boolean
}

const ControlledRadioField = (props: ControlledRadioFieldProps) => {
  const {
    control,
    formState,
    name,
    label,
    defaultValue,
    requried = false,
    data,
  } = props

  return (
    <>
      <Controller
        control={control}
        name={name}
        render={({ field, fieldState }) => (
          <RadioGroupField
            label={label}
            required={requried}
            error={!!fieldState.error}
            data={data.data.map(value => {
              return {
                label: value[data.namekey],
                value: value[data.idkey],
                labelPlacement: 'end',
                onChange: (
                  event: React.ChangeEvent<HTMLInputElement>,
                  checked: boolean,
                ) => {
                  field.onChange(event.target.value)
                },
                inputRef: field.ref,
                checked: field.value === value[data.idkey] ? true : false,
              }
            })}
          />
        )}
        defaultValue={defaultValue}
        rules={{ required: requried }}
      />
      {formState.errors[name] && (
        <ValidationAlert fieldError={formState.errors[name]} label={label} />
      )}
    </>
  )
}

export { ControlledRadioField }
