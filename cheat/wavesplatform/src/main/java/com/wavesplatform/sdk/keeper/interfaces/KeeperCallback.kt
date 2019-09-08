/*
 * Created by Eduard Zaydel on 23/8/2019
 * Copyright © 2019 Waves Platform. All rights reserved.
 */

package com.wavesplatform.sdk.keeper.interfaces

import com.wavesplatform.sdk.keeper.model.KeeperResult

interface KeeperCallback<T> {
    fun onSuccess(result: KeeperResult.Success<T>)
    fun onFailed(error: KeeperResult.Error)
}