package org.egovframe.cloud.userservice.domain.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Embeddable
public class UserFindPasswordId implements Serializable {

    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = -2267755880384011782L;

    /**
     * 이메일 주소
     */
    @Column(length = 50)
    private String emailAddr;

    /**
     * 요청 번호
     */
    private Integer requestNo;

    /**
     * 빌드 패턴 클래스 생성자
     *
     * @param emailAddr 이메일 주소
     * @param requestNo 요청 번호
     */
    @Builder
    public UserFindPasswordId(String emailAddr, Integer requestNo) {
        this.emailAddr = emailAddr;
        this.requestNo = requestNo;
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by java.util.HashMap.
     *
     * @return int a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(emailAddr, requestNo);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param object the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UserFindPasswordId)) return false;
        UserFindPasswordId that = (UserFindPasswordId) object;
        return Objects.equals(emailAddr, that.getEmailAddr()) &&
                Objects.equals(requestNo, that.getRequestNo());
    }

}
