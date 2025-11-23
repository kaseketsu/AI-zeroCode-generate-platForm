export enum codeGenTypeEnums {
  HTML = 'html',
  MULTI_FILE = 'html-multi',
  VUE_PROJECT = 'vue_project',
}

/**
 * 格式化代码生成类型
 * @param type 代码生成类型
 * @returns 格式化后的类型描述
 */
export const formatCodeGenType = (type: string | undefined): string => {
  if (!type) return '未知类型'

  const config = codeGenTypeConfig[type as codeGenTypeEnums]
  return config ? config.label : type
}

export const codeGenTypeConfig = {
  [codeGenTypeEnums.HTML]: {
    label: '原生 HTML 模式',
    value: codeGenTypeEnums.HTML,
  },
  [codeGenTypeEnums.MULTI_FILE]: {
    label: '原生多文件模式',
    value: codeGenTypeEnums.MULTI_FILE,
  },
  [codeGenTypeEnums.VUE_PROJECT]: {
    label: 'vue 项目模式',
    value: codeGenTypeEnums.VUE_PROJECT,
  },
}

export const codeGenTypeOptions = Object.values(codeGenTypeConfig).map((config) => ({
  label: config.label,
  value: config.value,
}))
