/**
 * SaveKart Toast Notification System
 * Ultra-sleek glassmorphic toasts with smooth fade animations
 */
const SaveKartToast = {
    container: null,

    init() {
        if (this.container) return;
        this.container = document.createElement('div');
        this.container.id = 'savekart-toast-container';
        this.container.style.cssText = `
            position: fixed;
            top: 24px;
            right: 24px;
            z-index: 99999;
            display: flex;
            flex-direction: column;
            gap: 12px;
            max-width: 380px;
            pointer-events: none;
        `;
        document.body.appendChild(this.container);
    },

    show(message, type = 'info', icon = '🔔') {
        this.init();

        const toast = document.createElement('div');
        toast.style.cssText = `
            pointer-events: auto;
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 14px 20px;
            border-radius: 14px;
            background: rgba(22, 35, 62, 0.85);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid ${type === 'success' ? 'rgba(34, 197, 94, 0.4)' : type === 'error' ? 'rgba(239, 68, 68, 0.4)' : 'rgba(88, 101, 255, 0.4)'};
            box-shadow: 0 12px 32px rgba(0, 0, 0, 0.35);
            color: #ffffff;
            font-family: 'Poppins', sans-serif;
            font-size: 0.9rem;
            font-weight: 500;
            transform: translateX(120%);
            transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.4s ease;
            opacity: 0;
        `;

        toast.innerHTML = `
            <span style="font-size: 1.2rem;">${icon}</span>
            <span style="flex: 1;">${message}</span>
            <span onclick="this.parentElement.remove()" style="cursor: pointer; opacity: 0.6; font-size: 1rem;">✕</span>
        `;

        this.container.appendChild(toast);

        // Animate In
        requestAnimationFrame(() => {
            toast.style.transform = 'translateX(0)';
            toast.style.opacity = '1';
        });

        // Auto Dismiss
        setTimeout(() => {
            toast.style.transform = 'translateX(120%)';
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 400);
        }, 4000);
    },

    showSuccess(message) {
        this.show(message, 'success', '✅');
    },

    showError(message) {
        this.show(message, 'error', '⚠️');
    },

    showInfo(message) {
        this.show(message, 'info', '💡');
    },

    showPriceAlert(message) {
        this.show(message, 'info', '📉');
    }
};
