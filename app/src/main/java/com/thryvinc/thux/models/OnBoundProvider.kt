package com.thryvinc.thux.models

interface OnBoundProvider {
    var onBound: ((Int, Int) -> Unit)?
}