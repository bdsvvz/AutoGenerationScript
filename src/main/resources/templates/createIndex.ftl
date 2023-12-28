IF NOT EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[DF__mhis_biz___manua__6FE86C85]') AND type = 'D')
BEGIN
    ALTER TABLE [dbo].[mhis_biz_infusion] ADD  CONSTRAINT [DF__mhis_biz___manua__6FE86C85]  DEFAULT ((0)) FOR [manual_flag]
END

GO