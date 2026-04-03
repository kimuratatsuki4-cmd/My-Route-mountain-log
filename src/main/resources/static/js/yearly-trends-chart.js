/**
 * アクティビティ一覧ページ（list.html）の年別統計データ（Yearly Trends）を表示するチャート描画用JavaScript
 * 
 * 【解説】
 * Chart.jsライブラリを使用して、年ごとのアクティビティ数、合計距離、合計獲得標高を
 * 一つの折れ線グラフ（複合グラフ）として描画します。
 */

document.addEventListener('DOMContentLoaded', function() {
    // 【解説: 変数の初期化】
    // thymeleafから渡された設定情報(chartConfig)を参照します。
    // 定義語やデータが存在しない場合は処理を中断またはデフォルト値を設定します。
    if (typeof chartConfig === 'undefined') {
        return; // 設定オブジェクトがなければ何もしない
    }

    const yearlyStats = chartConfig.yearlyStats || [];
    const labelActivities = chartConfig.labelActivities || 'Activities';
    const labelDistance = chartConfig.labelDistance || 'Distance (km)';
    const labelElevation = chartConfig.labelElevation || 'Elevation (m)';
    const labelAxisLeft = chartConfig.labelAxisLeft || 'Activities / Distance';
    const labelAxisRight = chartConfig.labelAxisRight || 'Elevation (m)';
    const labelNoData = chartConfig.labelNoData || 'No data available yet';

    const chartCanvas = document.getElementById('yearlyTrendsChart');
    if (!chartCanvas) {
        return;
    }

    // データが空の場合はグラフを描画せず、メッセージを表示する
    if (!yearlyStats || yearlyStats.length === 0) {
        chartCanvas.parentElement.innerHTML = 
            '<p style="text-align:center; padding: 60px 0; color: var(--color-text-muted); font-size: 0.85rem;">' + labelNoData + '</p>';
        return;
    }

    /**
     * 【解説: map関数の活用】
     * yearlyStats 配列の各要素から特定のプロパティだけを抽出し、
     * グラフ描画用の新しい配列 (labels, activitiesData など) を作成します。
     */
    const labels = yearlyStats.map(function(s) { return s.year; });
    const activitiesData = yearlyStats.map(function(s) { return s.totalActivities; });
    const distanceData = yearlyStats.map(function(s) { return s.totalDistance; });
    const elevationData = yearlyStats.map(function(s) { return s.totalElevation; });

    const ctx = chartCanvas.getContext('2d');

    // Chart.js インスタンスの生成と設定
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: labelActivities,
                    data: activitiesData,
                    borderColor: '#2d5a3d',
                    backgroundColor: 'rgba(45, 90, 61, 0.08)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4, // 折れ線の丸み
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    yAxisID: 'y' // 左側のY軸を使用
                },
                {
                    label: labelDistance,
                    data: distanceData,
                    borderColor: '#6ba3be',
                    backgroundColor: 'rgba(107, 163, 190, 0.08)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    yAxisID: 'y' // 左側のY軸を使用
                },
                {
                    label: labelElevation,
                    data: elevationData,
                    borderColor: '#c8956c',
                    backgroundColor: 'rgba(200, 149, 108, 0.08)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointHoverRadius: 6,
                    yAxisID: 'y1' // 右側のY軸を使用
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: { size: 12 }
                    }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    ticks: { font: { size: 12 } }
                },
                y: {
                    type: 'linear',
                    position: 'left',
                    title: { display: true, text: labelAxisLeft, font: { size: 11 } },
                    beginAtZero: true,
                    grid: { color: 'rgba(0,0,0,0.04)' }
                },
                y1: {
                    type: 'linear',
                    position: 'right',
                    title: { display: true, text: labelAxisRight, font: { size: 11 } },
                    beginAtZero: true,
                    grid: { drawOnChartArea: false } // 目盛り線が重ならないようにする
                }
            }
        }
    });
});
