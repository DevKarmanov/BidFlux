package van.karm.complaint.domain.model;

public enum ComplaintReason {
    SPAM,                     // Массовая рассылка, реклама, флуд
    SCAM,                     // Мошенничество, обман с целью выгоды
    OFFENSIVE_BEHAVIOR,       // Оскорбительное поведение, агрессия
    FRAUD,                    // Подделка данных, фальшивые аккаунты/сделки
    MISLEADING_INFORMATION,   // Ложная или вводящая в заблуждение информация
    INAPPROPRIATE_CONTENT,    // Неподобающий контент (насилие, откровенные материалы и т.п.)
    POLICY_VIOLATION,         // Нарушение правил платформы
    PRIVACY_VIOLATION,        // Разглашение личных данных без согласия
    IMPERSONATION,            // Выдача себя за другого человека/организацию
    OTHER                     // Другое (нестандартные случаи)
}
