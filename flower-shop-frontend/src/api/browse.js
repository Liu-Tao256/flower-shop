import request from '@/utils/request'

export const saveBrowseHistory = (data) => request.post('/browse-history', data)
export const getBrowseHistory = (params) => request.get('/browse-history', { params })
export const deleteBrowseHistory = (id) => request.delete(`/browse-history/${id}`)
export const clearBrowseHistory = () => request.delete('/browse-history/clear')