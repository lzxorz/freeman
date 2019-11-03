
import {$getDictItems} from '@/api'

/**
 * 获取字典选项数组
 * @param dictType 字典类型
 * @return [{lable:'显示文字‘, value:'字典值'},......]
 */
export function initDictOptions (dictType) {
  if (!dictType) { return [] }
  return $getDictItems({type: dictType})
}

/**
 * 根据字典值,获取显示文字
 * @param dictOptions 字典选项数据==>[{lable:'显示文字‘, value:'字典值'},......]
 * @return 显示文字
 */
export function getDictLabelByValue (dictOptions, value) {
  if (!dictOptions || !value) {
    return '未知'
  }
  let label = []
  value.split(',').forEach(v => {
    for (let i = 0; i < dictOptions.length; i++) {
      const oneDictItem = dictOptions[i]
      if (oneDictItem.value === v) {
        label.push(oneDictItem.label)
        break
      }
    }
  })

  return label.join(',')
}

/**
 * 字典选项数组转换为表格头部筛选数组
 * @param dictOptions  字典选项数组==>[{lable:'显示文字‘, value:'字典值'},......]
 * @param text  字典值
 * @return String
 */
/* export function getDictFilters (dictOptions) {
  if (!dictOptions) {
    return []
  }
  return dictOptions.map(option => {
    return {text: option.label, value: option.value}
  })
} */
