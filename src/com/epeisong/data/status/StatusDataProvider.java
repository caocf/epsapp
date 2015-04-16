package com.epeisong.data.status;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.utils.Detector;

/**
 * 根据状态获取数据列表
 * @author poet
 *
 */
public abstract class StatusDataProvider<T> {

    /**
     * 获取最新的数据。
     * @param status
     * @param size
     * @param edgeIndex 本地最新的数据的index
     * @return
     */
    public List<T> provideNewest(int status, int size, int edgeIndex) throws NetGetException {
        List<T> result = new ArrayList<T>();
        List<T> netList = getNewestFromNet(status, size, edgeIndex);
        if (edgeIndex == 0 || (netList != null && netList.size() >= size)) {
            // 本地没有数据，不论服务器是否有足够数据，直接return
            // 服务器返回的数据满足要求，直接return
            return netList;
        }
        // 服务器返回数据个数：0 <= netSize < size
        // 还差：dSize = size - netSize;
        int dSize = size;
        if (netList != null && netList.size() > 0) {
            dSize -= netList.size();
            result.addAll(netList);
        }
        List<T> olderFromDb = getOlderFromDb(dSize, edgeIndex + 1);
        if (olderFromDb == null || olderFromDb.size() < dSize) {
            // 本地数据个数不足，从服务器取
            getOlderFromNet(status, dSize, edgeIndex + 1);
        } else {
            List<Integer> indexList = getIndexList(olderFromDb);
            if (Detector.isBreak(edgeIndex + 1, indexList)) {
                // 数据不连续，从服务器直接取

            } else {

            }
        }
        return null;
    }
    

    /**
     * 获取服务器最新的数据。
     *   edgeIndex为0，获取服务器最新size个数据；
     *   edgeIndex不为0，获取大于edgeIndex的size个数据。若服务器数据大于size个，取最新的size个数据。
     * @param status
     * @param size
     * @param edgeIndex 本地最新的数据的index
     * @return
     */
    protected abstract List<T> getNewestFromNet(int status, int size, int edgeIndex) throws NetGetException;

    protected abstract List<T> getOlderFromNet(int status, int size, int edgeIndex) throws NetGetException;

    /**
     * 获取本地较旧的数据
     * @param size
     * @param edgeIndex
     * @return
     */
    protected abstract List<T> getOlderFromDb(int size, int edgeIndex);

    protected abstract List<Integer> getIndexList(List<T> list);
}
