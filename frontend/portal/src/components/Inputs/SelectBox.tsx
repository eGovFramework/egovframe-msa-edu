import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from 'react'

export type OptionsType = {
  value: ValueType
  label: string
}

export type SelectType = {
  selectedValue: ValueType
}

interface SelectBoxProps
  extends React.DetailedHTMLProps<
    React.SelectHTMLAttributes<HTMLSelectElement>,
    HTMLSelectElement
  > {
  options: OptionsType[]
  customHandleChange?: (e: React.ChangeEvent<HTMLSelectElement>) => void
}

const SelectBox = forwardRef<SelectType, SelectBoxProps>(
  (props: SelectBoxProps, ref) => {
    const { options, customHandleChange, ...rest } = props

    const [selectedState, setSelectedState] =
      useState<ValueType | undefined>(undefined)

    useEffect(() => {
      if (options.length > 0) {
        setSelectedState(options[0].value)
      }
    }, [options])

    const handleChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
      if (customHandleChange) {
        customHandleChange(e)
      }

      setSelectedState(e.target.value)
    }

    useImperativeHandle(ref, () => ({
      selectedValue: selectedState,
    }))

    return (
      <select onChange={handleChange} {...rest}>
        {options &&
          options.map(item => (
            <option key={`selectbox-${item.value}`} value={item.value}>
              {item.label}
            </option>
          ))}
      </select>
    )
  },
)

export { SelectBox }
