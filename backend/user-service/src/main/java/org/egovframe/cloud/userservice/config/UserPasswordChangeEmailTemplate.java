package org.egovframe.cloud.userservice.config;

public class UserPasswordChangeEmailTemplate {

    /**
     * 객체 생성 금지
     */
    private UserPasswordChangeEmailTemplate() throws IllegalStateException {
        throw new IllegalStateException("user password change email template class");
    }

    public static final String html = "<style type=\"text/css\">\n"
            + "	@import url(http://fonts.googleapis.com/earlyaccess/notosanskr.css);\n"
            + "	body {font-family:\"Noto Sans KR\", \"맑은 고딕\", sans-serif; font-weight:400; font-size:14px; color:#333; line-height:18px; -webkit-text-size-adjust:100%%; -moz-text-size-adjust:100%%; -ms-text-size-adjust:100%%;}\n"
            + "</style>\n"
            + "<div style=\"width:100%%; max-width:800px; margin:0 auto;\">\n"
            + "	<a href=\"%s\" target=\"_blank\" style=\"display:block; margin:35px;\"><div><img src=\"https://user-images.githubusercontent.com/80671095/130181337-16d978c6-71c4-4759-82b3-9a542f4c1aad.png\" alt=\"표준프레임워크 포털로고\" style=\"width:170px;\"></div></a>\n"
            + "	<table width=\"100%%\" align=\"center\" cellspacing=\"0\" border=\"0\" bgcolor=\"#fff\" style=\"margin:0 auto; border:35px solid #eee;\">\n"
            + "		<colgroup>\n"
            + "			<col width=\"165\">\n"
            + "			<col width=\"400\">\n"
            + "			<col width=\"165\">\n"
            + "		</colgroup>\n"
            + "		<thead>\n"
            + "			<tr>\n"
            + "				<th></th>\n"
            + "				<th height=\"100\"></th>\n"
            + "				<th></th>\n"
            + "			</tr>\n"
            + "			<tr>\n"
            + "				<th></th>\n"
            + "				<th align=\"center\" style=\"font-size:40px; font-weight:600;\">비밀번호 초기화 안내</th>\n"
            + "				<th></th>\n"
            + "			</tr>\n"
            + "			<tr>\n"
            + "				<th></th>\n"
            + "				<th height=\"40\" style=\"border-bottom:1px solid #e8e8e8\"></th>\n"
            + "				<th></th>\n"
            + "			</tr>\n"
            + "		</thead>\n"
            + "		<tbody>\n"
            + "			<tr>\n"
            + "				<td colspan=\"3\" height=\"60\"></td>\n"
            + "			</tr>\n"
            + "			<tr>\n"
            + "				<td colspan=\"3\" style=\"padding-left:45px; color:#666; font-size:24px; line-height:36px;\">\n"
            + "					안녕하세요. <b style=\"color:#333;\">%s</b> 회원님.<br /><br />\n"
            + "					비밀번호 초기화 관련하여 안내드립니다.<br />\n"
            + "					회원님의 계정 비밀번호를 초기화할 수 있는 URL을 알려드립니다.<br /><br />\n"
            + "					<span style=\"color:#1e75d6; font-weight:600;\">[비밀번호 초기화] 버튼</span>으로 접속하여 비밀번호를 초기화 하신 후<br />서비스를 계속해서 이용해주시기 바랍니다.<br /><br />\n"
            + "					해당 링크는 발송후 1시간 동안만 유효합니다.<br /><br />\n"
            + "					감사합니다.\n"
            + "				</td>\n"
            + "			</tr>\n"
            + "			<tr>\n"
            + "				<td colspan=\"3\" height=\"60\"></td>\n"
            + "			</tr>\n"
            + "			<tr>\n"
            + "				<td colspan=\"3\" align=\"center\">\n"
            + "					<a href=\"%s\" target=\"_blank\" style=\"height:60px; padding:10px 50px; color:#fff; font-size:20px; font-weight:600; text-align:center; line-height:58px; border:0; background:#1e75d6; border-radius:5px; text-decoration: none;\">비밀번호 초기화</a>\n"
            + "				</td>\n"
            + "			</tr>\n"
            + "			<tr>\n"
            + "				<td colspan=\"3\" height=\"100\"></td>\n"
            + "			</tr>\n"
            + "		</tbody>\n"
            + "	</table>\n"
            + "	<div style=\"padding:25px 0; color:#999; font-size:15px; text-align:center; line-height:28px;\">\n"
            + "		(C) 표준프레임워크 포털   All Rights Reserved.\n"
            + "	</div>\n"
            + "</div>\n";

}
